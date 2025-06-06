package org.magnasoft.jacoco.server.sessions;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;

import java.util.function.Consumer;

/**
 * A session that holds execution data collected by multiple JaCoCo agents having the same session ID.
 */
class Session implements Consumer<ExecutionData> {
    private final ExecutionDataStore executionDataStore= new ExecutionDataStore();

    @Override
    public void accept(final ExecutionData executionData) {
        synchronized (executionDataStore) {
            // merges the execution data.
            executionDataStore.put(executionData);
        }
    }
}
