package net.flytre.aim_plus.scope;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class TemporaryState<T> {

    private @Nullable T state;
    private @Nullable Date stateEntranceTime;
    private @Nullable TimeRule remainingTimeRule;

    public TemporaryState() {
        state = null;
        stateEntranceTime = new Date(System.currentTimeMillis());
        remainingTimeRule = null;
    }

    public void enterState(T state, TimeRule rule) {
        this.state = state;
        this.stateEntranceTime = new Date(System.currentTimeMillis());
        this.remainingTimeRule = rule;
    }

    private void update() {
        if (remainingTimeRule == null || !remainingTimeRule.check()) {
            state = null;
            stateEntranceTime = new Date(System.currentTimeMillis());
        }
    }


    public boolean isInState() {
        update();
        return state != null;
    }

    public T getState() {
        update();
        return state;
    }

    public Date getStateEntranceTime() {
        update();
        return stateEntranceTime;
    }

    public long millisecondsInState() {
        return System.currentTimeMillis() - getStateEntranceTime().getTime();
    }

    public interface TimeRule {
        /**
         * @return whether the app should remain in the state or exit it.
         */
        boolean check();
    }

    public static class InfiniteTimeRule implements TimeRule {

        @Override
        public boolean check() {
            return true;
        }
    }

    public static class UntilDateRule implements TimeRule {

        private Date exitDate;

        public UntilDateRule(Date exitDate) {
            this.exitDate = exitDate;
        }

        public static UntilDateRule randomMillisecondsFromNow(long min, long max) {
            return new TemporaryState.UntilDateRule(new Date(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(min, max + 1)));
        }

        @Override
        public boolean check() {
            //if the current time is before the exit time then return true
            return new Date(System.currentTimeMillis()).compareTo(exitDate) < 0;
        }
    }

}

