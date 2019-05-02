package com.statemachine.statemachinejpapersistanceexample20.config;

import static com.statemachine.statemachinejpapersistanceexample20.enums.Status.*;

import com.statemachine.statemachinejpapersistanceexample20.enums.Event;
import com.statemachine.statemachinejpapersistanceexample20.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<Status, Event> {

    @Autowired
    private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineConfigurationConfigurer<Status, Event> config) throws Exception {
        config.withPersistence().runtimePersister(stateMachineRuntimePersister());
    }

    @Override
    public void configure(StateMachineStateConfigurer<Status, Event> states) throws Exception {
        states.withStates()
              .initial(ROOT)
              .state(Status.ROOT)
              .and()

              .withStates()
              .parent(ROOT)
              .initial(Status.S0)
              .state(S0)
              .state(Status.S1)
              .fork(Status.FORK_S2)
              .state(Status.S2)
              .join(Status.JOIN_S2)
              .state(S3)
              .state(S4)
              .state(Status.S5)

              .and()
              .withStates()
              .parent(Status.S2)
              .initial(Status.S21I)
              .state(S21_IN_PROGRESS)
              .state(Status.S21_NOT_REQUIRED)
              .end(Status.S21E)

              .and()
              .withStates()
              .parent(Status.S2)
              .initial(Status.S22I)
              .state(Status.S22_IN_PROGRESS)
              .state(Status.S22_PASSED)
              .end(S22E)

              .and()
              .withStates()
              .parent(Status.S2)
              .initial(Status.S23I)
              .state(Status.S23_IN_PROGRESS)
              .state(Status.S23_PASSED)
              .end(S23E)

              .and()
              .withStates()
                .parent(S2)
                .initial(Status.S24I)
                .choice(Status.CHOICE_S24)
                .state(Status.S24_IN_PROGRESS)
                .state(Status.S24_PASSED)
                .state(Status.S24_NOT_REQUIRED)
                .end(Status.S24E)

        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Status, Event> transitions) throws Exception {
        transitions.withExternal()
                   .source(S0)
                   .target(S1)
                   .event(Event.NEW)
                   .and()

                   .withExternal()
                   .source(S1)
                   .target(FORK_S2)
                   .and()

                   .withFork() // FORK
                   .source(FORK_S2)
                   .target(S2)
                   .and()

                   // S21
                   .withExternal()
                   .source(S21I)
                   .target(S21_NOT_REQUIRED)
                   .event(Event.S21_NOT_REQUIRED)
                   .action((ctx) -> System.out.println("s21-not-required"))
                   .and()

                   .withExternal()
                   .source(S21_NOT_REQUIRED)
                   .target(S21E)
                   .action(ctx -> System.out.println("s21-done"))
                   .and()

                   // s22
                   .withExternal()
                   .source(S22I)
                   .target(S22_IN_PROGRESS)
                   .event(Event.S22_IN_PROGRESS)
                   .action(ctx -> System.out.println("s22-in-progress"))
                   .and()

                   .withExternal()
                   .source(S22_IN_PROGRESS)
                   .target(S22_PASSED)
                   .event(Event.S22_PASSED)
                   .action((ctx) -> System.out.println("s22-passed"))
                   .and()

                   .withExternal()
                   .source(S22_PASSED)
                   .target(S22E)
                   .action(ctx -> System.out.println("s22-done"))
                   .and()

                   // s23
                   .withExternal()
                   .source(S23I)
                   .target(S23_IN_PROGRESS)
                   .action(ctx -> System.out.println("s23-inprogress"))
                   .and()

                   .withExternal()
                   .source(S23_IN_PROGRESS)
                   .target(S23_PASSED)
                   .event(Event.S23_PASSED)
                   .action((ctx) -> System.out.println("s23-passed"))
                   .and()

                   .withExternal()
                   .source(S23_PASSED)
                   .target(S23E)
                   .action(ctx -> System.out.println("s23-done"))
                   .and()

                   // s24
                   .withExternal()
                   .source(S24I)
                   .target(CHOICE_S24)
                   .action(ctx -> System.out.println("s24-choice"))
                   .and()

                   .withChoice()
                   .source(CHOICE_S24)
                   .first(S24_IN_PROGRESS, ctx-> false)
                   .last(S24_NOT_REQUIRED, ctx -> System.out.println("s24-not-required"))
                   .and()

                   .withExternal()
                   .source(S24_NOT_REQUIRED)
                   .target(S24E)
                   .and()

                   .withExternal()
                   .source(S24_IN_PROGRESS)
                   .target(S24_PASSED)
                   .event(Event.S24_PASSED)
                   .action((ctx) -> System.out.println("s24-passed"))
                   .and()

                   .withExternal()
                   .source(S24_PASSED)
                   .target(S24E)
                   .action(ctx -> System.out.println("s24-done"))
                   .and()

                   // JOIN
                   .withJoin()
                   .source(S2)
                   .target(JOIN_S2)
                   .and()

                   .withExternal()
                   .source(JOIN_S2)
                   .target(S3)
                   .guard(ctx -> true)
                   .action(ctx -> System.out.println("s3"))
                   .and()

                   .withExternal()
                   .source(S3)
                   .target(S4)
                   .guard(toggledVarTrue())
                   .action(ctx -> System.out.println("toggle_status == 1"))
                   .and()

                   .withInternal()
                   .source(ROOT)
                   .event(Event.TOGGLE_STATUS_0)
                   .action(ctx -> ctx.getExtendedState().getVariables().put("toggle_status", "0"))
                   .and()

                   .withInternal()
                   .source(ROOT)
                   .event(Event.TOGGLE_STATUS_1)
                   .action(ctx -> ctx.getExtendedState().getVariables().put("toggle_status", "1"))

        ;
    }

    private Guard<Status, Event> toggledVarTrue() {

        return context -> {
            boolean guard = String.valueOf(context.getExtendedState().get("toggle_status", String.class)).equalsIgnoreCase("1");
            return guard;
        };
    }

    @Bean
    public StateMachineRuntimePersister<Status, Event, String> stateMachineRuntimePersister() {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<Status, Event> stateMachineService(StateMachineFactory<Status, Event> stateMachineFactory,
                                                                  StateMachineRuntimePersister<Status, Event, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService(stateMachineFactory, stateMachineRuntimePersister);
    }
}
