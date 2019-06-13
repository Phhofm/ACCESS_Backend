package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;

import java.util.EnumSet;

public class EvalMachineFactory {

    public static final String EXTENDED_VAR_SUBMISSION_ID = "submissionId";
    public static final String EXTENDED_VAR_NEXT_STEP = "nextStep";

    public static StateMachine<EvalMachine.States, EvalMachine.Events> initSMForSubmission(String submissionId) throws Exception {

        StateMachineBuilder.Builder<EvalMachine.States, EvalMachine.Events> builder
                = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates()
                .initial(EvalMachine.States.SUBMITTED, routeAction())
                .end(EvalMachine.States.FINISHED)
                .states(EnumSet.allOf(EvalMachine.States.class));

        builder.configureTransitions()
                .withExternal()
                .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.GRADING)
                .event(EvalMachine.Events.GRADE)
                .action(gradeAction())
                .and()
                .withExternal()
                .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.DELEGATE)
                .event(EvalMachine.Events.DELEGATE)
                .action(delegateAction())
                .and()
                .withExternal()
                .source(EvalMachine.States.DELEGATE).target(EvalMachine.States.RETURNING)
                .event(EvalMachine.Events.RETURN)
                .and()
                .withExternal()
                .source(EvalMachine.States.RETURNING).target(EvalMachine.States.GRADING)
                .event(EvalMachine.Events.GRADE)
                .and()
                .withExternal()
                .source(EvalMachine.States.GRADING).target(EvalMachine.States.FINISHED)
                .event(EvalMachine.Events.FINISH);

        StateMachine machine = builder.build();
        machine.getExtendedState().getVariables().put(EXTENDED_VAR_SUBMISSION_ID, submissionId);
        return machine;
    }

    public static String extractProcessStep(StateMachine machine){
        return machine.getExtendedState().getVariables().get(EXTENDED_VAR_NEXT_STEP).toString();
    }

    private static Action<EvalMachine.States, EvalMachine.Events> routeAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                System.err.println("ZZZZ - route action");
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, RouteSubmissionStep.class.getName());
            }
        };
    }

    private static Action<EvalMachine.States, EvalMachine.Events> delegateAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                System.err.println("ZZZZ - delegate action");
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, DelegateCodeExecStep.class.getName());
            }
        };
    }

    private static Action<EvalMachine.States, EvalMachine.Events> gradeAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                System.err.println("ZZZZ - grade action");
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, GradeSubmissionStep.class.getName());
            }
        };
    }
}
