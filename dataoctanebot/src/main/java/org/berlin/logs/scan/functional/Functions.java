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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic abstract operations with IHandler/Function. Treat objects as first
 * order functions. These routines are not application specific and should work
 * for general purpose use-cases.
 * 
 * Example use case: Determine run time for a method call.
 * 
 * <pre>
 *   Functions.time(someFunc, input);
 *   Will return a pair with the result and function execution time in milliseconds.
 * </pre>
 */
public class Functions {

	/**
	 * Execute the function and determine the function run time.
	 * 
	 * @param f
	 *            IHandler<A,B>
	 * @param input
	 *            A
	 * @return Pair<B,Long>
	 */
	public static <A, B> Pair<B, Double> time(final IHandler<A, B> f, final A input) {
		final long start = System.nanoTime();
		final B res = f.execute(input);
		final long end = System.nanoTime();
		return new Pair<B, Double>(res, (end - start) / 1000000.0);
	}

	/**
	 * Execute the function and determine the function run time.
	 * 
	 * @param f
	 *            IHandler<A,B>
	 * @param input
	 *            A
	 * @return Pair<B,Long>
	 */
	public static <A, B> Pair<B, Long> timems(final IHandler<A, B> f, final A input) {
		final long start = System.currentTimeMillis();
		final B res = f.execute(input);
		final long end = System.currentTimeMillis();
		return new Pair<B, Long>(res, (end - start));
	}

	/**
	 * Return the function and determine the function run time. Do not pass execute
	 * function.
	 * 
	 * @param f
	 *            IHandler<A,B>
	 * @return IHandler<A,Pair<B,Long>>
	 */
	public static <A, B> IHandler<A, Pair<B, Long>> timef(final IHandler<A, B> f) {
		return new IHandler<A, Pair<B, Long>>() {
			private static final long serialVersionUID = 1L;
			public Pair<B, Long> execute(final A a) {
				final long start = System.currentTimeMillis();
				final B b = f.execute(a);
				final long end = System.currentTimeMillis();
				return new Pair<B, Long>(b, (end - start));
			}
		};
	}

	/**
	 * Apply the given function to each element and append to the stringbuffer.
	 *
	 * @param list
	 *            Collection<A>
	 * @param buf
	 *            StringBuffer
	 * @param f
	 *            The function to map apply to the elements in the list.
	 * @return StringBuffer
	 */
	public static <A, B> StringBuffer mapbuf(final Collection<A> list, final StringBuffer buf, final IHandler<A, B> f) {
		for (final A obj : list) {
			if (f != null) {
				buf.append(f.execute(obj));
			}
		}
		return buf;
	}

	/**
	 * Apply the given function to each element and append to the stringbuffer.
	 *
	 * @param list
	 *            Collection<A>
	 * @param buf
	 *            StringBuffer
	 * @param f
	 *            The function to map apply to the elements in the list.
	 * @return StringBuffer
	 */
	public static <A, B> StringBuffer mapbuf(final Collection<A> list, final StringBuffer buf, final IHandler<A, B> f,
			final String delim) {
		for (final A obj : list) {
			if (f != null) {
				buf.append(f.execute(obj) + delim);
			}
		}
		return buf;
	}

	/**
	 * Return a list by applying the given function to each element.
	 *
	 * @param list
	 *            Collection<A>
	 * @param f
	 *            The function to map apply to the elements in the list.
	 * @return Collection<B>
	 */
	public static <A, B> Collection<B> map(final Collection<A> list, final IHandler<A, B> f) {
		final Collection<B> newlist = new ArrayList<B>();
		for (final A obj : list) {
			if (f != null) {
				newlist.add(f.execute(obj));
			}
		}
		return newlist;
	}

	/**
	 * Apply the given function to each element.
	 *
	 * @param list
	 *            Collection<A>
	 * @param f
	 *            The function to map apply to the elements in the list.
	 */
	public static <A, B> void mapv(final Collection<A> list, final IHandler<A, B> f) {
		for (final A obj : list) {
			if (f != null) {
				f.execute(obj);
			}
		}
	}

	/**
	 * Apply the given function to each element.
	 *
	 * @param list
	 *            Collection<A>
	 * @param f
	 *            The function to map apply to the elements in the list.
	 */
	public static <A, B> void foreach(final Collection<A> list, final IHandler<A, B> f) {
		for (final A obj : list) {
			if (f != null) {
				// Execute the function on object of TYPE-A (B ignored)
				f.execute(obj);
			}
		}
	}

	/**
	 * Apply the given function to each element and append to the stringbuffer.
	 *
	 * @param list
	 *            Collection<A>
	 * @param buf
	 *            StringBuilder
	 * @param f
	 *            The function to map apply to the elements in the list.
	 * @return StringBuilder
	 */
	public static <A, B> StringBuilder mapbuf(final Collection<A> list, final StringBuilder buf,
			final IHandler<A, B> f) {
		for (final A obj : list) {
			if (f != null) {
				buf.append(f.execute(obj));
			}
		}
		return buf;
	}

	/**
	 * The identity function.
	 *
	 * @return The identity function.
	 */
	public static <A> IHandler<A, A> identity() {
		return new IHandler<A, A>() {
			private static final long serialVersionUID = 1L;
			public A execute(final A a) {
				return a;
			}
		};
	}

	/**
	 * Composition. This is useful when you need to write a function that uses the
	 * result of some other call.
	 * 
	 * <pre>
	 * f(g(input))
	 * a -> b -> c
	 * Functions.<Integer, Integer, Integer>compose(Functions.inc, Functions.inc).execute(0);
	 * </pre>
	 * 
	 * Result of call is 2.
	 * 
	 * @param f
	 *            IHandler<B,C>
	 * @param g
	 *            IHandler<A,B>
	 * @return Function composed of the given arguments.
	 */
	public static <A, B, C> IHandler<A, C> compose(final IHandler<B, C> f, final IHandler<A, B> g) {
		return new IHandler<A, C>() {
			private static final long serialVersionUID = 1L;
			public C execute(final A a) {
				return f.execute(g.execute(a));
			}
		};
	}

	/**
	 * Add plus one to the element.
	 * 
	 * @return
	 */
	public static final IHandler<Integer, Integer> inc = new IHandler<Integer, Integer>() {
		private static final long serialVersionUID = 1L;

		public Integer execute(final Integer a) {
			return a == null ? 1 : a + 1;
		}

	};

	/**
	 * Add plus one to the element.
	 * 
	 * @return
	 */
	public static final IHandler<Integer, Integer> dec = new IHandler<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		public Integer execute(final Integer a) {
			return a == null ? -1 : a - 1;
		}

	};

	/**
	 * Negate.
	 * 
	 * @return
	 */
	public static final IHandler<Boolean, Boolean> not = new IHandler<Boolean, Boolean>() {
		private static final long serialVersionUID = 1L;
		public Boolean execute(final Boolean a) {
			return !a;
		}
	};

	/**
	 * Convert the object to string representation of that object.
	 * 
	 * @return
	 */
	public static final IHandler<Object, String> toString = new IHandler<Object, String>() {
		private static final long serialVersionUID = 1L;
		public String execute(final Object a) {
			return a == null ? "" : a.toString();
		}
	};

	/**
	 * Negate.
	 * 
	 * @return
	 */
	public static final IHandler<Object, Object> obj = new IHandler<Object, Object>() {
		private static final long serialVersionUID = 1L;
		public Object execute(final Object a) {
			return a;
		}
	};

	/**
	 * Given a string object, return a string.
	 * 
	 * @return
	 */
	public static final IHandler<String, String> str = new IHandler<String, String>() {
		private static final long serialVersionUID = 1L;
		public String execute(final String a) {

			return a == null ? "" : a;
		}
	};

	/**
	 * Negate.
	 * 
	 * @return
	 */
	public static final IHandler<Object, Boolean> isNull = new IHandler<Object, Boolean>() {
		private static final long serialVersionUID = 1L;
		public Boolean execute(final Object a) {
			return a == null;
		}
	};

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param a
	 *            A
	 * @param f
	 *            IHandler<A,B>
	 * @return B
	 */
	public static <A, B> B when(final A a, final IHandler<A, B> f) {
		if ((f == null) || (a == null)) {
			return null;
		} else {
			return f.execute(a);
		}
	}

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param z
	 *            IHandler<C,Boolean>
	 * @param f
	 *            IHandler<A,B>
	 * @param c
	 *            C
	 * @param a
	 *            A
	 * @return B
	 */
	public static <A, B, C> B when(final IHandler<C, Boolean> z, final IHandler<A, B> f, final C c, final A a) {
		if (z.execute(c)) {
			return f.execute(a);
		}
		return null;
	}

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param z
	 *            IHandler<C,Boolean>
	 * @param f
	 *            IHandler<A,B>
	 * @param c
	 *            C
	 * @param a
	 *            A
	 * @return B
	 */
	public static void when(final FBool fbool, final When f) {
		if (fbool.execute(new Object())) {
			f.execute();
		}
	}

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param c
	 *            boolean
	 * @param f
	 *            IHandler<A,B>
	 * @param a
	 *            A
	 * @return B
	 */
	public static <A, B> B when(final boolean c, final IHandler<A, B> f, final A a) {
		if (c) {
			return f.execute(a);
		}
		return null;
	}

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param c
	 *            boolean
	 * @param f
	 *            IHandler<A,B>
	 * @param a
	 *            A
	 * @return B
	 */
	public static void when(final boolean c, final When f) {
		if (c) {
			f.execute();
		}
	}

	/**
	 * When input arguments aren't null, execute f.
	 * 
	 * @param c
	 *            boolean
	 * @param f
	 *            IHandler<A,B>
	 * @param a
	 *            A
	 * @return B
	 */
	public static void when(final boolean c) {
		if (c) {
			new When().execute();
		}
	}

	/**
	 * Simple object, contains execute method with no return.
	 */
	public static class When {
		/**
		 * Process routine.
		 */
		public void execute() {
		}
	}

	/**
	 * Simple object, contains execute method with no return.
	 */
	public static interface FBool extends IHandler<Object, Boolean> {

	}

	public static class FVoid extends Object {

	}

	/**
	 * Filters objects from this list by returning only object which produce true
	 * when the given function is applied to them.
	 * 
	 * @param inList
	 *            Collection<A>
	 * @param f
	 *            The function to filter on.
	 * @return A new list where all the elements match the given function.
	 */
	public static <A> Collection<A> filter(final Collection<A> inList, final IHandler<A, Boolean> f) {
		final Collection<A> newlist = new ArrayList<A>();
		for (final A obj : inList) {
			final Boolean res = f.execute(obj);
			if ((res != null) && res.booleanValue()) {
				newlist.add(obj);
			}
		}
		return newlist;
	}

	/**
	 * Creates a list, the first argument determines how many items should be taken
	 * from the list passed as the second argument.
	 * 
	 * @param inList
	 *            Collection<A>
	 * @param f
	 *            The function to filter on.
	 * @return A new list where all the elements match the given function.
	 */
	public static <A> Collection<A> take(final int n, final Collection<A> inList) {
		final Collection<A> newlist = new ArrayList<A>();
		if (n <= 0) {
			return newlist;
		}
		if (inList == null) {
			return newlist;
		}
		int i = 1;
		for (final A obj : inList) {
			if (i <= n) {
				newlist.add(obj);
			}
			i++;
		}
		return newlist;
	}

	public static Boolean and(final boolean input, final IHandler<Boolean, Boolean> f1,
			final IHandler<Boolean, Boolean> f2) {
		return f1.execute(input) && f2.execute(input);
	}

	public static Boolean or(final boolean input, final IHandler<Boolean, Boolean> f1,
			final IHandler<Boolean, Boolean> f2) {
		return f1.execute(input) || f2.execute(input);
	}

	/**
	 * Creates a map, the first argument determines how many items should be taken
	 * from the map passed as the second argument.
	 * 
	 * @param inList
	 *            Collection<A>
	 * @param f
	 *            The function to filter on.
	 * @return A new list where all the elements match the given function.
	 */
	public static <A, B> Map<A, B> take(final int n, final Map<A, B> inList) {
		if (inList == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		final Class c = inList.getClass();
		Map<A, B> newlist = null;
		try {
			newlist = (Map<A, B>) c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			newlist = new LinkedHashMap<A, B>();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			newlist = new LinkedHashMap<A, B>();
		}
		if (n <= 0) {
			return newlist;
		}

		int i = 1;
		for (final A obj : inList.keySet()) {
			if (i <= n) {
				newlist.put(obj, inList.get(obj));
			}
			i++;
		}
		return newlist;
	}

	/**
	 * Returns a list of integers from the given <code>from</code> value (inclusive)
	 * to the given <code>to</code> value (exclusive).
	 * 
	 * @param from
	 *            int
	 * @param to
	 *            int
	 * @return Collection<Integer>
	 */
	public static Collection<Integer> range(final int from, final int to) {
		if (from >= to) {
			return new ArrayList<Integer>();
		} else {
			final Collection<Integer> x = new ArrayList<Integer>();
			for (int i = from; i < to; i++) {
				x.add(i);
			}
			return x;
		}
	}

	/**
	 * Wrapper to create a pair objects
	 * 
	 * @param a
	 *            A
	 * @param b
	 *            B
	 * @return Pair<A,B>
	 */
	public static <A, B> Pair<A, B> tupl(final A a, final B b) {
		return new Pair<A, B>(a, b);
	}

	/**
	 * Create tuple list with initial value for a and b.
	 * 
	 * @param a
	 *            A
	 * @param b
	 *            B
	 * @return Collection<Pair<A,B>>
	 */
	public static <A, B> Collection<Pair<A, B>> tlist(final A a, final B b) {
		final Collection<Pair<A, B>> list = new ArrayList<Pair<A, B>>();
		list.add(tupl(a, b));
		return list;
	}

	/**
	 * Given a list of pairs, convert to a pair of two lists.
	 * 
	 * @param list
	 *            Collection<Pair<A,B>>
	 * @return Pair<Collection<A>,Collection<B>>
	 */
	public static <A, B> Pair<Collection<A>, Collection<B>> unzip(final Collection<Pair<A, B>> list) {
		// Return empty list.
		if (list == null) {
			final Collection<A> emptya = Collections.unmodifiableList(new ArrayList<A>());
			final Collection<B> emptyb = Collections.unmodifiableList(new ArrayList<B>());
			return new Pair<Collection<A>, Collection<B>>(emptya, emptyb);
		}

		final List<A> a = new ArrayList<A>();
		final List<B> b = new ArrayList<B>();
		// otherwise loop through the list and extract the tupl data
		for (final Pair<A, B> pair : list) {
			a.add(pair.getFirst());
			b.add(pair.getSecond());
		} // End of for

		final List<A> newa = Collections.unmodifiableList(a);
		final List<B> newb = Collections.unmodifiableList(b);
		return new Pair<Collection<A>, Collection<B>>(newa, newb);
	}

	/**
	 * Simple class to modify one element. This can be used to modify primitives to
	 * methods have final arguments.
	 * 
	 * @param <T>
	 */
	public static final class Mutable<T> {
		private T mutable;
		/**
		 * Constructor.
		 * 
		 * @param m
		 */
		public Mutable(final T m) {
			this.mutable = m;
		}
		/**
		 * Set the mutable.
		 * 
		 * @param val
		 * @return
		 */
		public synchronized T set(final T val) {
			this.mutable = val;
			return mutable;
		}
		public synchronized T get() {
			return mutable;
		}
		@Override
		public String toString() {
			return String.valueOf(mutable);
		}
	}

	/**
	 * Object of abstract type with only one field and setters and getters for that
	 * member.
	 */
	public static abstract class Type<T> {
		private final T value;
		/**
		 * Constructor.
		 * 
		 * @param val
		 */
		public Type(final T val) {
			this.value = val;
		}
		public synchronized T get() {
			return this.value;
		}
		/**
		 * Execute and operate on the value object.
		 * 
		 * @return
		 */
		public abstract T execute();
	}

} // End of class
