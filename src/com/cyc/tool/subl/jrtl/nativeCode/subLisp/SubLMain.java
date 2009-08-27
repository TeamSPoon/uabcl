/*
 * SubLMain.java
 *
 * Created on December 13, 2005, 1:41 PM
 */

package com.cyc.tool.subl.jrtl.nativeCode.subLisp;

// Internal imports
import com.cyc.tool.subl.jrtl.nativeCode.type.core.AbstractSubLSequence;
import com.cyc.tool.subl.jrtl.translatedCode.sublisp.*;
import com.cyc.tool.subl.util.*;
import com.cyc.tool.subl.ui.*;

// External imports
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLProcess;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLEnvironment;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLPackage;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLString;
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import com.cyc.tool.subl.jrtl.nativeCode.type.operator.SubLFunction;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLNil;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationEmitter;


/**
 * Typical arguments: -i "/cyc/top/init/jrtl-init.lisp"
 * Typical Java params: -server -Xms256m -Xmx776m -Xss1m -XX:MaxPermSize=128m
 * Typical working directory: /home/<user>/cvs/head/cycorp/cyc/top/
 * @author goolsbey, tbrussea
 */
public final class SubLMain {
	private static Logger logger = Logger.getLogger(SubLMain.class.getCanonicalName());
  
  //// Constructors
  
  /** There should only ever be one instance of this */
  private SubLMain() {
    
  }
  
  //// Public Area
  
  public static final InputStream ORIGINAL_IN_STREAM = System.in;
  public static final PrintStream ORIGINAL_OUT_STREAM = System.out;
  public static final PrintStream ORIGINAL_ERR_STREAM = System.err;
  
  public static final SubLMain me = new SubLMain();
  
  public void doSystemCleanupAndExit(int code) {
    System.exit(code);
  }
  
  
  //// Initializers
  
  /** This is unfortunately public so it can be called by unit tests */
  public static void initializeSubL(String[] args) {
    initializeLowMemoryDetection();
    SubLPackage.initPackages();
    SubLFiles.initialize(Packages.me);
    SubLPackage.setCurrentPackage(SubLPackage.SUBLISP_PACKAGE);
    SubLSymbol sym = CommonSymbols.EQ; // @hack to make sure this get initialized first
    SubLFiles.initialize(Equality.me);
    SubLFiles.initialize(SubLSpecialOperatorDeclarations.me);
    SubLFiles.initialize(Numbers.me);
    SubLFiles.initialize(Strings.me);
    SubLFiles.initialize(Types.me);
    SubLFiles.initialize(ConsesLow.me);
    SubLFiles.initialize(Alien.me);
    SubLFiles.initialize(Values.me);
    SubLFiles.initialize(Characters.me);
    BinaryFunction.initialize(); // this must come after Equality -APB
    
    SubLFiles.initialize(Semaphores.me);
    SubLFiles.initialize(Dynamic.me);
    SubLFiles.initialize(Errors.me);
    SubLFiles.initialize(Environment.me);
    SubLFiles.initialize(Eval.me);
    SubLFiles.initialize(Filesys.me);
    SubLFiles.initialize(Functions.me);
    SubLFiles.initialize(Guids.me);
    SubLFiles.initialize(Hashtables.me);
    //SubLFiles.initialize(Keyhashes.me);
    SubLFiles.initialize(Locks.me);
    SubLFiles.initialize(ReadWriteLocks.me);
    SubLFiles.initialize(Mapper.me);
    SubLFiles.initialize(Mapping.me);
    SubLFiles.initialize(PrintLow.me);
    SubLFiles.initialize(Processes.me);
    SubLFiles.initialize(Regex.me);
    SubLFiles.initialize(Sequences.me);
    SubLFiles.initialize(Sort.me);
    SubLFiles.initialize(Storage.me);
    SubLFiles.initialize(StreamsLow.me);
    SubLFiles.initialize(Structures.me);
    SubLFiles.initialize(Sxhash.me);
    SubLFiles.initialize(Symbols.me);
    SubLFiles.initialize(SystemInfo.me);
    SubLFiles.initialize(Tcp.me);
    SubLFiles.initialize(Threads.me);
    SubLFiles.initialize(Time.me);
    SubLFiles.initialize(UserIO.me);
    SubLFiles.initialize(Vectors.me);
    
    // SubLFiles.initialize(SubL.me);
    // translated RTL extensions
    // these are in the order they are initialized in the C RTL
    SubLFiles.initialize(print_high.me);
    SubLFiles.initialize(streams_high.me);
    SubLFiles.initialize(stream_macros.me);
    SubLFiles.initialize(print_macros.me);
    SubLFiles.initialize(print_functions.me);
    
    SubLFiles.initialize(conses_high.me);
    SubLFiles.initialize(hashtables_high.me);
    SubLFiles.initialize(bytes.me);
    SubLFiles.initialize(environment.me);
    SubLFiles.initialize(foreign.me);
    SubLFiles.initialize(format.me);
    SubLFiles.initialize(reader.me);
    SubLFiles.initialize(random.me);
    SubLFiles.initialize(cdestructuring_bind.me);
    SubLFiles.initialize(complex_special_forms.me);
    SubLFiles.initialize(character_names.me);
    SubLFiles.initialize(math_utilities.me);
    SubLFiles.initialize(compatibility.me);
    SubLFiles.initialize(time_high.me);
    SubLFiles.initialize(condition_macros.me);
    SubLFiles.initialize(thread_macros.me);
    SubLFiles.initialize(subl_benchmarks.me);
    
    ZeroArityFunction.initialize(); // this must come after ConsesLow -APB
    UnaryFunction.initialize(); // this must come after ConsesLow -APB
    
    AbstractSubLSequence.init();
    
    me.mainReader = new SubLReader();
  }
  
  public static void initializeTranslatedSystems() {
    // @todo make this more flexible once we translate multiple systems
    // or want to ship it without the dependency on cyc
    SubLPackage.setCurrentPackage(SubLPackage.CYC_PACKAGE);
    try {
      Eval.initialize_subl_interface_file(SubLObjectFactory.
          makeString("com.cyc.cycjava.cycl.cycl"));
    } catch (Exception e) {
      // ignore
    } finally {
      SubLPackage.setCurrentPackage(SubLPackage.CYC_PACKAGE);
    }
    PrintLow.registerJRTLPrintMethods();
  }
  
  public static String getCommandLineArg(String argName) {
    return (String)me.argNameToArgValueMap.get(argName);
  }
  
  public static SubLString getWorldFileName() {
    String fileName = (String)me.argNameToArgValueMap.get("-w");
    return (fileName == null) ? null : SubLObjectFactory.makeString(fileName);
  }
  
  public static SubLString getInitializationFileName() {
    String fileName = (String)me.argNameToArgValueMap.get("-i");
    return (fileName == null) ? null : SubLObjectFactory.makeString(fileName);
  }
  
  public static String getInitializationForm() {
    return (String)me.argNameToArgValueMap.get("-f");
  }
  
  public static boolean shouldQuitAfterExecutingInitializationForm() {
    Boolean value = (Boolean)me.argNameToArgValueMap.get("-q");
    return (value == Boolean.TRUE);
  }
  
  public static boolean shouldRunInBackground() {
    Boolean value = (Boolean)me.argNameToArgValueMap.get("-b");
    return (value == Boolean.TRUE);
  }
  
  public static boolean shouldRunReadloopInGUI() {
    Boolean value = (Boolean)me.argNameToArgValueMap.get("-gui");
    return (value == Boolean.TRUE);
  }
  
  public static SubLObject get_red_object() {
    return Errors.unimplementedMethod("SubLMain.get_red_object()");
  }
  
  public static final boolean isInitialized() {
    return isInitialized;
  }
  
  public static final void setIsInitialized() {
    isInitialized = true;
  }
  
  public static void setMainReader(SubLReader reader) {
    me.mainReader = reader;
  }
  
  public void processCommandLineArgs(String[] args) {
    for (int i = 0, size = args.length; i < size; i++) {
      String arg = args[i];
      if (noArgCommandLineArgs.contains(arg)) {
        argNameToArgValueMap.put(arg, Boolean.TRUE);
      } else if (argRequiredCommandLineArgs.contains(args[i])) {
        if (i == size) {
          throw new SubLException("Not enough command line arguments given for: " + arg);
        }
        argNameToArgValueMap.put(arg, args[++i]);
      } else {
        throw new SubLException("Got invalid command line argument: " + args[i]);
      }
    }
  }
  
  public static final void registerLowMemoryCallback(SubLFunction func) {
    if (func == null) {
      Errors.error("Unable to register low memory callback for null function.");
    }
    lowMemoryCallbacks.add(func);
  }
  
  public static final void lowMemorySituation() {
    logger.warning("Low memory situation...trying to reclaim memory.");
    for (SubLFunction func : lowMemoryCallbacks) {
      try {
        Functions.funcall(func);
      } catch (Exception e) {
        Errors.warn("Warning: calling low memory callback function: " + func);
      }
    }
    logger.warning("Low memory situation...finished trying to reclaim memory.");
    Storage.room(CommonSymbols.UNPROVIDED);
  }
  
  public static SubLReader getMainReader() {
    return me.mainReader;
  }
  
  
  //// Private Area
  
  
  private static class MemoryListener implements javax.management.NotificationListener {
    public void handleNotification(final Notification notification, final Object handback) {  
        String notifType = notification.getType();
        if (notifType.equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED) ||
            notifType.equals(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED)) {
          lowMemorySemaphore.release();
        }
    }
  }
  
  private static void initializeLowMemoryDetection() {
    MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    NotificationEmitter emitter = (NotificationEmitter) mbean;
    MemoryListener listener = new MemoryListener();
    emitter.addNotificationListener(listener, null, null);
    List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
    for (MemoryPoolMXBean pool : pools) {
       //@todo other garbage collector may use diffeerent names
      MemoryType curPoolType = pool.getType();
      if (pool.getName().contains("Perm") || pool.getName().contains("Tenured") || pool.getName().contains("Old")) {
        long max = pool.getCollectionUsage().getMax();
        long percent90 = (long)(max * .9) + 1;
        if (pool.isCollectionUsageThresholdSupported()) {
          pool.setCollectionUsageThreshold(percent90);
          if (pool.isUsageThresholdSupported()) {
            pool.setUsageThreshold(percent90);
          }
        } else {
          if (pool.isUsageThresholdSupported()) {
            pool.setUsageThreshold(percent90);
          } else {
            Errors.warn("Unable to detect low memory situations.");
          }
        }
      }
    }

    SubLObjectFactory.makeProcess(SubLObjectFactory.makeString("Low Memory Scavenger"), new Runnable() {
      public void run() {
        while (true) {
          lowMemorySemaphore.acquireUninterruptibly();
          Runtime rt = Runtime.getRuntime();
          long totalMemory = rt.totalMemory();
          long freeMemory = rt.freeMemory();
          long usedMemory = totalMemory - freeMemory;
          long eightyPercentMemory = (long)(totalMemory * .8);
          if (usedMemory >= eightyPercentMemory) {
            lowMemorySituation();
          }
          lowMemorySemaphore.drainPermits();
        }
      }
    });
  }
  
  
  //// Internal Rep
  
  private SubLReader mainReader = null;
  private static Set noArgCommandLineArgs = new HashSet();
  private static Set argRequiredCommandLineArgs = new HashSet();
  private Map argNameToArgValueMap = new HashMap();
  private static boolean isInitialized = false;
  private static final Set<SubLFunction> lowMemoryCallbacks = new HashSet<SubLFunction>();
  private static final Semaphore lowMemorySemaphore = new Semaphore(0);
  
  static {
    noArgCommandLineArgs.add("-gui");
    noArgCommandLineArgs.add("-q");
    noArgCommandLineArgs.add("-b");
    argRequiredCommandLineArgs.add("-i");
    argRequiredCommandLineArgs.add("-f");
    argRequiredCommandLineArgs.add("-w");
  }
  
  private static void writeSystemInfo() {
    logger.info("Start time: " + new Date());
    logger.info("Lisp implementation: " + Environment.lisp_implementation_type().getString());
    logger.info("JVM: " + System.getProperty("java.vm.vendor") + " "
    + " " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
    try {
      logger.info("Current KB: " 
        + Eval.eval(reader.read_from_string(SubLObjectFactory.makeString("(kb-version-string)"), //use reader to avoid direct code dependency on cycl
        CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED, 
        CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED)).getString());
    } catch (Exception e) {
      logger.info("KB: <none>"); //assume error implies KB not loaded
    }
    try {
      logger.info("Patch Level: " 
        + Eval.eval(reader.read_from_string(SubLObjectFactory.makeString("(cyc-revision-string)"), //use reader to avoid direct code dependency on cycl
        CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED, 
        CommonSymbols.UNPROVIDED, CommonSymbols.UNPROVIDED)).getString());
    } catch (Exception e) {
      logger.info("Patch level: <unknown>");
    }
    logger.info("Running on: " + Environment.machine_instance().getString());
    logger.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
    + " (" + System.getProperty("os.arch") + ")");
    logger.info("Working directory: " + System.getProperty("user.dir"));
  }
  
  //// Main
  
  /**
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    final Runtime rt = Runtime.getRuntime();
    SubLMain.me.processCommandLineArgs(args);
    try {
      SubLProcess subLProcess = new SubLProcess("Initial Lisp Listener") {
        public void safeRun() {
          try {
            //if (!shouldRunInBackground()) {
              logger.info("Starting LarKC.");
           // }
            long startTime = System.currentTimeMillis();
            SubLMain.me.initializeSubL(args);
            SubLMain.me.initializeTranslatedSystems();
            long endTime = System.currentTimeMillis();
            double theTime = ((endTime - startTime) / 1000.0);
            if (!shouldRunInBackground()) {
              logger.fine("Internal initialization time = " + theTime + " secs.");
            }
            //SubLMain.me.doSystemCleanupAndExit();
            try {
            //Thread.currentThread().sleep(20000);
            } catch (Exception e) {
            }
            startTime = System.currentTimeMillis();

            SubLString worldFile = getWorldFileName();
            // @todo do something with worldFile if not null here -APB
            SubLString initFile = getInitializationFileName();
            if (initFile != null) {
              try {
                Eval.load(initFile);
              } catch (Exception e) {
                Errors.handleError("Failed to load initialization file: " + initFile, e);
              }
            }
            String initForm = getInitializationForm();
            if (initForm != null) {
              try {
                SubLString initFormString = SubLObjectFactory.makeString(initForm);
                SubLObject form = reader.read_from_string(initFormString, T, UNPROVIDED, UNPROVIDED, UNPROVIDED, UNPROVIDED);
                form.eval(SubLEnvironment.currentEnvironment());
              } catch (Exception e) {
                Errors.handleError("Failed evaluation initialization form: " + initForm, e);
              }
            }
            if (shouldQuitAfterExecutingInitializationForm()) {
              SubLMain.me.doSystemCleanupAndExit(0);
            }
            if (!shouldRunInBackground()) {//TODO: For LarKC remove console
              setIsInitialized();
              endTime = System.currentTimeMillis();
              theTime = ((endTime - startTime) / 1000.0);
              logger.fine("Initialization time = " + theTime + " secs.");
              //writeSystemInfo();
              
              Storage.room(SubLNil.NIL);
              StreamsLow.$terminal_io$.getValue().toOutputStream().flush();
              if (shouldRunReadloopInGUI()) {
                SubLMain.setMainReader(SubLReaderPanel.startReadloopWindow());
              }
              SubLMain.getMainReader().doReadLoop();
            }
          } catch (RuntimeException e) { 
            logger.severe("Initial Lisp Listener Exiting Now");
            throw e;
          } finally {
            //logger.severe("Initial Lisp Listener Exiting Now");
          }
        }
      };
      SubLThreadPool.getDefaultPool().execute(subLProcess);
    } catch (Exception e) {
      Errors.handleError(e);
    }
  }
  
}


