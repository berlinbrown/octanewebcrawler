/**
 * Copyright (c) 2006-2011 Berlin Brown.  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 8/15/2011
 *  
 * Description: LogFile Searcher.  Search log file and build statistics from the scan.
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */

package org.berlin.logs.scan;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Utility class to work with null references.
 * @version $Revision: 1.0 $
 */
public class NullRef<T> implements Serializable {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    
    /**
     * Utility to check if string is empty.
     * 
     * @param str
     * @return boolean
     */
    public static boolean isEmptyTrim(final String str) {
        return (str == null) || (str.trim().length() == 0);
    }

    /**
     * Simple utility to check if string is empty.
     * 
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(final String str) {
        return (str == null) || (str.length() == 0);
    }
    
    /**
     * Method hasValue.
     * @param testObj Object
     * @return boolean
     */
    public static boolean hasValue(final Object testObj) {
        boolean rc = false;
        if (null != testObj) {
            rc = true;
        }
        return rc;
    }

    /**
     * Check if string has value, also check for str.trim is empty.
     * @param testString
     * @return boolean
     */
    public static boolean hasValue(final String testString) {
        return !isEmptyTrim(testString);
    }    

    /**
     * Return true if does not have value.
     * 
     * @param testObj Object
     * @return boolean
     */
    public static boolean nil(final Object testObj) {
        return !hasValue(testObj);
    }

    /**
     * Check if string has value, also check for str.trim is empty.
     * Return true if null or empty.
     * 
     * @param testString
     * @return boolean
     */
    public static boolean nil(final String testString) {
        return !hasValue(testString);
    }
    
    /**
     * Method get.
     * @param inVal Short
     * @return Short
     */
    public Short get(final Short inVal) {
        return (inVal == null) ? 0 : inVal;        
    }
    /**
     * Method get.
     * @param inVal Long
     * @return Long
     */
    public Long get(final Long inVal) {
        return (inVal == null) ? 0 : inVal;        
    }
    /**
     * Method get.
     * @param inVal Double
     * @return Double
     */
    public Double get(final Double inVal) {
        return (inVal == null) ? 0.0d : inVal;        
    }
    /**
     * Method get.
     * @param inVal Float
     * @return Float
     */
    public Float get(final Float inVal) {
        return (inVal == null) ? 0.0f : inVal;        
    }
    /**
     * Method get.
     * @param inVal Integer
     * @return Integer
     */
    public Integer get(final Integer inVal) {
        return (inVal == null) ? 0 : inVal;        
    }
    /**
     * Method get.
     * @param inVal Integer
     * @return Integer
     */
    public Boolean get(final Boolean inVal) {
        return (inVal == null) ? false : inVal;        
    }    
    
    /**
     * Attempt to get object, if null create instance
     * with default constructor.
     * 
     * @param inVal T
     * @return T
     */
    public T get(final T inVal) {
        if (inVal == null) {
            try {
                return getTypeParameterClass().newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();                
            }            
        }
        return inVal;
    }    
    
    /**
     * Attempt to get object, if null create instance
     * with default constructor.
     * 
     * 'When' object reference is null, create instance of type or return input parameter.  
     * 
     * @param inVal T
     * @return T
     */
    public T when(final T inVal) {
        return get(inVal);
    }
    /**
     * Attempt to get object, if null create instance
     * with default constructor.
     * 
     * 'When' object reference is null, create instance of type or return input parameter.  
     * 
     * @param inVal T
     * @return T
     */
    public T ifNull(final T inVal) {
        return get(inVal);
    }
    /**
     * Attempt to get object, if null create instance
     * with default constructor.
     * 
     * 'When' object reference is null, create instance of type or return input parameter.  
     * 
     * @param inVal T
     * @return T
     */
    public T notNull(final T inVal) {
        return get(inVal);
    }
    
    /**
     * Same as get but force call to handle exceptions.
     * 
     * @param inVal
     * @return T
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public T getUnsafe(final T inVal) throws IllegalAccessException, InstantiationException {
        return getTypeParameterClass().newInstance();                               
    }
            
    /**
     * Method getTypeParameterClass.
     * @return Class<T>
     */
    @SuppressWarnings ("unchecked")
    public Class<T> getTypeParameterClass() {    
        final Type type = getClass().getGenericSuperclass();
        final ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }
    
} // End of the class //
