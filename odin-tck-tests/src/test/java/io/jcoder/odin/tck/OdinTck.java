/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin.tck;

import javax.inject.Inject;
import javax.inject.Named;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import io.jcoder.odin.DefaultInjectionContext;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.annotation.component.ComponentRegistrar;
import io.jcoder.odin.annotation.component.DefaultComponentRegistrar;
import io.jcoder.odin.annotation.component.Registration;
import junit.framework.Test;

public class OdinTck {
    
    /**
     * org.atinject.tck.auto.Car is implemented by Convertible. 
        •@Drivers Seat isimplemented by DriversSeat. 
        •Seat isimplemented by Seat itself, and Tire by Tire itself(not subclasses). 
        •Engine is implemented by V8Engine. 
        •@Named("spare") Tire is implemented by SpareTire. 
        •The following classes may also be injected directly: Cupholder, SpareTire, and FuelTank. 
        Static and private member injection support is optional, but if yourinjector supports those features, it must pass the respective tests. Ifstatic member injection is supported, the static members of the followingtypes shall also be injected once: Convertible, Tire, and SpareTire. 
     */
    
    @Component
    public static class OdinCarComponent {
        @Inject
        @Registration
        private Convertible car;
        
        @Inject
        @Registration
        private Seat seat;
        
        @Inject
        @Registration
        private Tire tire;
        
        @Inject
        @Registration
        private V8Engine engine;
        
        @Inject
        @Registration
        private Cupholder cupholder;
        
        @Inject
        @Registration
        private FuelTank fuelTank;
        
        @Inject
        @Registration
        @Drivers
        private DriversSeat driversSeat;
        
        @Inject
        @Registration
        @Named("spare")
        private SpareTire spareTire;
    }
    
    public static Test suite() {
        InjectionContext injectionContext = new DefaultInjectionContext();
        ComponentRegistrar componentRegistrar = new DefaultComponentRegistrar(injectionContext);
        componentRegistrar.addComponent(OdinCarComponent.class);
        componentRegistrar.initialize();
        
        Car car = injectionContext.get(Car.class);
        
        return Tck.testsFor(car, false, false);
    }
}
