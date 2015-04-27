package org.apache.velocity.tools.generic;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Set;

/**
 * Tool for working with Sets in Velocity templates.
 * Also provides methods to perform the following actions to Sets:
 * <ul>
 *   <li>Check if it is empty.</li>
 *   <li>Check if it contains a certain element.</li>
 * </ul>
 *
 * <p><pre>
 * Example uses:
 *  $set.size($primes)        -> 4
 *  set.isEmpty($primes)     -> false
 *  $set.contains($primes, 7) -> true
 *
 * Example toolbox.xml config (if you want to use this with VelocityView):
 * &lt;tool&gt;
 *   &lt;key&gt;set&lt;/key&gt;
 *   &lt;scope&gt;application&lt;/scope&gt;
 *   &lt;class&gt;org.apache.velocity.tools.generic.SetTool&lt;/class&gt;
 * &lt;/tool&gt;
 * </pre></p>
 *
 * <p>This tool is entirely threadsafe, and has no instance members.
 * It may be used in any scope (request, session, or application).
 * </p>
 *
 * @author <a href="mailto:shinobu@ieee.org">Shinobu Kawai</a>
 * @version $Id: $
 * @since VelocityTools 1.2
 */
public class SetTool
{

    /**
     * Default constructor.
     */
    public SetTool() {
    	// DO NOTHING
    }

    /**
     * Gets the size of a Set
     * It will return null under the following conditions:
     * <ul>
     *   <li><code>set</code> is null.</li>
     *   <li><code>set</code> is not a set</li>
     * </ul>
     * @param set the Set object.
     * @return the size of the Set.
     */
    public Integer size(Object set)
    {
        if (!isSet(set))
        {
            return null;
        }

        return Integer.valueOf(((Set) set).size());
    }

    /**
     * Checks if an object is a Set.
     * @param object the object to check.
     * @return <code>true</code> if the object is a Set.
     */
    public boolean isSet(Object object)
    {
        return object instanceof Set;
    }

    /**
     * Checks if a Set is empty.
     * @param set the Set to check.
     * @return <code>true</code> if the Set is empty.
     */
    public Boolean isEmpty(Object set)
    {
        Integer size = size(set);
        if (size == null)
        {
            return null;
        }

        return Boolean.valueOf(size.intValue() == 0);
    }

    /**
     * Checks if a Set contains a certain element.
     * @param set the Set to check.
     * @param element the element to check.
     * @return <code>true</code> if the Set contains the element.
     */
    public Boolean contains(Object set, Object element)
    {
        if (!isSet(set))
        {
            return null;
        }

        return Boolean.valueOf(((Set) set).contains(element));
    }
}