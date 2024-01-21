package org.example.model;

import lombok.Value;

/**
 * Represents an event related to gold transactions.
 * This class encapsulates the details of a change in gold amount, including the delta and the resulting balance.
 */
@Value(staticConstructor = "of")
public class Event {

    /**
     * The change in gold amount (positive for addition, negative for subtraction).
     */
    Integer goldDelta;

    /**
     * The current gold balance after applying the gold delta.
     */
    Integer currentGoldBalance;
}
