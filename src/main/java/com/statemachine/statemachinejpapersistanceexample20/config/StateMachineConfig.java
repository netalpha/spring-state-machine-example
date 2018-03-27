package com.statemachine.statemachinejpapersistanceexample20.config;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

import com.statemachine.statemachinejpapersistanceexample20.enums.Events;
import com.statemachine.statemachinejpapersistanceexample20.enums.States;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> { 
	
	@Autowired
	private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
		.withPersistence()
			.runtimePersister(stateMachineRuntimePersister());
    }
    

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
		        .withStates()
		        .initial(States.I)
		        .states(EnumSet.allOf(States.class));
    }
    
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
              .withExternal()
                .source(States.I).target(States.A).event(Events.E1);
    }
 
    


	@Bean
	public StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister() {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}
    
    @Bean
	public StateMachineService<States, Events> stateMachineService(StateMachineFactory<States, Events> stateMachineFactory,
			StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister) {
		return new DefaultStateMachineService<States, Events>(stateMachineFactory, stateMachineRuntimePersister);
	}
    
    
  	 @Bean
	    public StateMachineListener<States, Events> listener() {
	
	        return new StateMachineListenerAdapter<States, Events>() {
	            @Override
	            public void stateChanged(State<States, Events> from, State<States, Events> to) {
	            	System.out.println("Listerner : In state chnaged");
	                if (from == null) {
	                    System.out.println("State machine initialised in state " + to.getId());
	                } else {
	                    System.out.println("State changed from " + from.getId() + " to " + to.getId());
	                }
	            }
	        };
	    }
	
}
