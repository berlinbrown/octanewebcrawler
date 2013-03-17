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
package org.berlin.logs.scan.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Add functionality to sort by value.
 * 
 * @param <K>
 * @param <V>
 */
public class SortedMap<K, V extends Comparable<V>> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = 1L;

  public SortedMap() {
    super();
  }

  public SortedMap(final Map<K, V> map) {
    super(map);
  }
  /**
   * Sort collection.
   * 
   * @param <T>
   * @param c
   * @return
   */
  public static <T extends Comparable<? super T>> List<T> sort(final Collection<T> c) {
    final List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
  }
  
  /**
   * Sort generic collection.
   * 
   * @param c
   * @param cp
   * @return
   */
  public static List sort(final Collection c, final Comparator cp) {
    final List list = new ArrayList(c);
    java.util.Collections.sort(list, cp);
    return list;
  }
  
  /**
   * Sort by value.
   */
  public void sortByValue() {
    final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(entrySet());
    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
      public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
        if (entry1 == null) {
          return 1;
        }
        if (entry2 == null) {
          return -1;
        }
        return entry1.getValue().compareTo(entry2.getValue());
      }

    });
    this.clear();
    for (Map.Entry<K, V> entry : list) {
      this.put(entry.getKey(), entry.getValue());
    }
  }

} // End of the Class //