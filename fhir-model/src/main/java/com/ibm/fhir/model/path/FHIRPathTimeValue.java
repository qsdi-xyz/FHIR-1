/*
 * (C) Copyright IBM Corp. 2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.model.path;

import static com.ibm.fhir.model.path.util.FHIRPathUtil.getTemporal;
import static com.ibm.fhir.model.path.util.FHIRPathUtil.getTemporalAmount;
import static com.ibm.fhir.model.path.util.FHIRPathUtil.getTimePrecision;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Objects;

import com.ibm.fhir.model.path.util.FHIRPathUtil.TimePrecision;
import com.ibm.fhir.model.path.visitor.FHIRPathNodeVisitor;

public class FHIRPathTimeValue extends FHIRPathAbstractNode implements FHIRPathTemporalValue {    
    private static final DateTimeFormatter TIME_PARSER_FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral("T")
            .appendPattern("HH")
            .optionalStart()
                .appendPattern(":mm")
                .optionalStart()
                    .appendPattern(":ss")
                    .optionalStart()
                        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                    .optionalEnd()
                .optionalEnd()
            .optionalEnd()
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();
    
    private final LocalTime time;
    private final TimePrecision timePrecision;
    private final Temporal temporal;
    
    protected FHIRPathTimeValue(Builder builder) {
        super(builder);
        time = builder.time;
        timePrecision = builder.timePrecision;
        temporal = getTemporal(time);
    }
    
    @Override
    public boolean isTimeValue() {
        return true;
    }
    
    public TemporalAccessor temporalAccessor() {
        return time;
    }
    
    public LocalTime time() {
        return time;
    }
    
    @Override
    public TimePrecision timePrecision() {
        return timePrecision;
    }
    
    @Override
    public Temporal temporal() {
        return temporal;
    }
    
    public static FHIRPathTimeValue timeValue(String text) {
        return FHIRPathTimeValue.builder(LocalTime.parse(text, TIME_PARSER_FORMATTER), getTimePrecision(text)).build();
    }
    
    private static FHIRPathTimeValue timeValue(LocalTime time) {
        return FHIRPathTimeValue.builder(time, getTimePrecision(time)).build();
    }
    
    public static FHIRPathTimeValue timeValue(String name, LocalTime time) {
        return FHIRPathTimeValue.builder(time, getTimePrecision(time)).name(name).build();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(type, time, timePrecision);
    }
    
    public static Builder builder(LocalTime time, TimePrecision timePrecision) {
        return new Builder(FHIRPathType.SYSTEM_TIME, time, timePrecision);
    }
    
    public static class Builder extends FHIRPathAbstractNode.Builder {
        private final LocalTime time;
        private final TimePrecision timePrecision;
        
        private Builder(FHIRPathType type, LocalTime time, TimePrecision timePrecision) {
            super(type);
            this.time = time;
            this.timePrecision = timePrecision;
        }
        
        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }
        
        @Override
        public Builder path(String path) {
            return (Builder) super.path(path);
        }
        
        @Override
        public Builder value(FHIRPathSystemValue value) {
            return this;
        }
        
        @Override
        public Builder children(FHIRPathNode... children) {
            return this;
        }
        
        @Override
        public Builder children(Collection<FHIRPathNode> children) {
            return this;
        }

        @Override
        public FHIRPathTimeValue build() {
            return new FHIRPathTimeValue(this);
        }
    }
    
    public FHIRPathTimeValue add(FHIRPathQuantityValue quantityValue) {
        Temporal temporal = getTemporal(time);
        TemporalAmount temporalAmount = getTemporalAmount(quantityValue);
        return timeValue(LocalTime.from(temporal.plus(temporalAmount)));
    }

    public FHIRPathTimeValue subtract(FHIRPathQuantityValue quantityValue) {
        Temporal temporal = getTemporal(time);
        TemporalAmount temporalAmount = getTemporalAmount(quantityValue);
        return timeValue(LocalTime.from(temporal.minus(temporalAmount)));
    }

    @Override
    public boolean isComparableTo(FHIRPathNode other) {
        if (other instanceof FHIRPathTimeValue || other.getValue() instanceof FHIRPathTimeValue) {
            FHIRPathTimeValue timeValue = (other instanceof FHIRPathTimeValue) ? (FHIRPathTimeValue) other : (FHIRPathTimeValue) other.getValue();
            return time.getClass().equals(timeValue.time().getClass()) && 
                    timePrecision.equals(timeValue.timePrecision());
        }
        return false;
    }

    @Override
    public int compareTo(FHIRPathNode other) {
        if (!isComparableTo(other)) {
            throw new IllegalArgumentException();
        }
        FHIRPathTimeValue timeValue = (other instanceof FHIRPathTimeValue) ? (FHIRPathTimeValue) other : (FHIRPathTimeValue) other.getValue();
        return time.compareTo(timeValue.time());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FHIRPathNode)) {
            return false;
        }
        FHIRPathNode other = (FHIRPathNode) obj;
        if (!isComparableTo(other)) {
            return false;
        }
        FHIRPathTimeValue timeValue = (other instanceof FHIRPathTimeValue) ? (FHIRPathTimeValue) other : (FHIRPathTimeValue) other.getValue();
        return Objects.equals(time, timeValue.time());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(time);
    }

    @Override
    public String toString() {
        return TIME_PARSER_FORMATTER.format(time);
    }

    @Override
    public void accept(FHIRPathNodeVisitor visitor) {
        visitor.visit(this);
    }
}
