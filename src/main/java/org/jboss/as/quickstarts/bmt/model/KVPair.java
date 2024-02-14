/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.bmt.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity for storing key value pairs into a database.
 *
 * @author Mike Musgrove
 */
@Entity
@Table(name = "BMT_KVPair")
public class KVPair implements Serializable {
    /** Default value included to remove warning. **/
    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true)
    private String bmtKey;

    @Column
    private String bmtValue;

    public KVPair() {
    }

    public KVPair(String bmtKey, String bmtValue) {
        setBmtKey(bmtKey);
        setBmtValue(bmtValue);
    }

    public String getBmtKey() {
        return bmtKey;
    }

    public void setBmtKey(String bmtKey) {
        this.bmtKey = bmtKey;
    }

    public String getBmtValue() {
        return bmtValue;
    }

    public void setBmtValue(String bmtValue) {
        this.bmtValue = bmtValue;
    }

    public String toString() {
        return bmtKey + "=" + bmtValue;
    }
}
