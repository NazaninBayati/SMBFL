#include "../../FaultSeeds.h" 

package de.susebox.java.lang;

import java.util.Hashtable;
import de.susebox.java.lang.ExtNullPointerException;

public final class EnvironmentProvider {
  public static Environment getEnvironment(Object obj) {
    Environment env = null;

#ifdef FAULT_3
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif

    if (obj != null && _environmentMap != null) {
#ifdef FAULT_3
#else
      synchronized(_syncMonitor) {
#endif
        Object iterObj = obj;
          
        do {
          if (iterObj instanceof Class) {
            env     = (Environment)_environmentMap.get(new EnvironmentKey((Class)iterObj));
#ifdef FAULT_1
            iterObj = ((Class)iterObj).getClass();
#else
            iterObj = ((Class)iterObj).getSuperclass();
#endif
          } else {
            env = (Environment)_environmentMap.get(iterObj);
#ifdef FAULT_2
	    iterObj = iterObj.getSuperclass();
#else
            iterObj = iterObj.getClass();
#endif
          }
          if (env != null) {
            break;
          }
        } while (iterObj instanceof Class);
#ifdef FAULT_3
#else
      }
#endif
    }
    
    if (env == null) {
#ifdef FAULT_3
#else
      synchronized(_syncMonitor) {
#endif
        if (_defaultEnvironment == null) {
          _defaultEnvironment = new DefaultEnvironment();
        }
        env = _defaultEnvironment;
#ifdef FAULT_3
#else
      }
#endif
    }
    return env;
  }
  
  public static void setEnvironment(Object obj, Environment env) throws NullPointerException {
#ifdef FAULT_3    
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif     
    if (obj == null) {
      throw new ExtNullPointerException("No object given.");
    } else if (env == null) {
      throw new ExtNullPointerException("No environment given.");
    }

    synchronized(_syncMonitor) {
      if (_environmentMap == null) {
        _environmentMap = new Hashtable();
      }
       
      if (obj instanceof Class) {
        _environmentMap.put(new EnvironmentKey((Class)obj), env);
      } else {
        _environmentMap.put(obj, env);
      }
    }
  }
  
  public static void removeEnvironment(Object obj) {
#ifdef FAULT_3    
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif
    if (obj != null && _environmentMap != null) {
      if (obj instanceof Class) {
        _environmentMap.remove(new EnvironmentKey((Class)obj));
      } else {
        _environmentMap.remove(obj);
      }
    }
  }
  
  static final class EnvironmentKey {
    public EnvironmentKey(Class cl) {
#ifdef FAULT_3    
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif
      synchronized(this) {
        _class  = cl;
        _thread = Thread.currentThread();
      }
    }
    
    public boolean equals(Object obj) {
#ifdef FAULT_3    
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif
      if (obj == this) {
        return true;
      } else if (obj == null) {
        return false;
      } else if ( ! (obj instanceof EnvironmentKey)) {
        return false;
      } else {
        EnvironmentKey key = (EnvironmentKey)obj;
        
        if (_thread == key._thread && _class.equals(key._class)) {
          return true;
        } else {
          return false;
        }
      }
    }
    
    public int hashCode() {
#ifdef FAULT_3    
    // This method is not covered by any test case.
    System.out.println("=================FAULT_3===============");
#endif
      return (_thread.hashCode() << 4) + _class.getName().hashCode();
    }

    private Class   _class = null;
    private Thread  _thread = null;
  }
   
  private static DefaultEnvironment _defaultEnvironment = null;
  private static Hashtable          _environmentMap     = null;
  private static Object             _syncMonitor        = new Object();
}
