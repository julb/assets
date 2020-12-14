package me.julb.applications.dataprocessing.configurations.beans;

import lombok.Getter;
import lombok.Setter;

/**
 * The job execution metrics.
 * <P>
 * @author Airbus.
 */
@Getter
@Setter
public class CustomJobExecutionMetrics {

    //@formatter:off
     /**
     * The processedItemsCount attribute.
     * -- GETTER --
     * Getter for {@link #processedItemsCount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #processedItemsCount} property.
     * @param processedItemsCount the value to set.
     */
     //@formatter:on
    private Long processedItemsCount = 0L;

}
