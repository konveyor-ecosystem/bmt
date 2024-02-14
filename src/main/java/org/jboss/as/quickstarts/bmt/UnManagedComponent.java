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
package org.jboss.as.quickstarts.bmt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;

import java.util.List;

import org.jboss.as.quickstarts.bmt.model.KVPair;

/**
 * A class for updating a database table within a JTA transaction. Since the class is only a simple CDI bean the developer is
 * responsible for both controlling the life cycle of the Entity Manager and for transaction demarcation.
 *
 * @author Mike Musgrove
 */
@ApplicationScoped
public class UnManagedComponent {
    /*
     * Inject a UserTransaction for manual transaction demarcation (this object is thread safe)
     */
    @Inject
    @PersistenceUnit("primary")
    private EntityManager entityManager;

    public String updateKeyValueDatabase(String key, String value) {
        try {
            Log.info("Beginning user-managed transaction");
            QuarkusTransaction.begin();

            // make some transactional changes
            String result = updateKeyValueDatabaseLogic(key, value);

            /*
             * Note that the default scope of entities managed by the EM is transaction. Thus once the transaction commits the
             * entity will be detached from the EM. See also the comment in the finally block below.
             */
            QuarkusTransaction.commit();

            return result;
        } catch (Exception e) {
            QuarkusTransaction.rollback();

            /*
             * An application cannot handle any of the other exceptions raised by begin and commit so we just catch the generic
             * exception. The meaning of the other exceptions is:
             *
             * NotSupportedException - the thread is already associated with a transaction HeuristicRollbackException - should
             * not happen since the example is interacting with a single database HeuristicMixedException - should not happen
             * since the example is interacting with a single database SystemException - the TM raised an unexpected error.
             * There is no standard way of handling this error (another reason why CMT are preferable to managing them
             * ourselves)
             */
            return e.getMessage();
        }
    }

    /**
     * Utility method for updating a key value database.
     *
     * @param key if null or zero length then list all pairs
     * @param value if key exists then associate value with it, otherwise create a new pair
     * @return the new value of the key value pair or all pairs if key was null (or zero length).
     */
    public String updateKeyValueDatabaseLogic(String key, String value) {
        StringBuilder sb = new StringBuilder();

        if (key == null || key.length() == 0) {
            // list all key value pairs
            @SuppressWarnings("unchecked")
            final List<KVPair> list = entityManager.createQuery("select k from KVPair k").getResultList();

            for (KVPair kvPair : list)
                sb.append(kvPair.getBmtKey()).append("=").append(kvPair.getBmtValue()).append(',');

        } else {
            KVPair kvPair;

            if (value == null) {
                // retrieve the value associated with key
                kvPair = new KVPair(key, value);

                entityManager.refresh(kvPair);
            } else {
                kvPair = entityManager.find(KVPair.class, key);

                if (kvPair == null) {
                    // insert into the key/value table
                    kvPair = new KVPair(key, value);
                    entityManager.persist(kvPair);
                } else {
                    // update an existing row in the key/value table
                    kvPair.setBmtValue(value);
                    entityManager.persist(kvPair);
                }
            }

            sb.append(kvPair.getBmtKey()).append("=").append(kvPair.getBmtValue());
        }

        return sb.toString();
    }
}
