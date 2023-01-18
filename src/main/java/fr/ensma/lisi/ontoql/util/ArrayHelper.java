/*********************************************************************************
* This file is part of OntoQL Project.
* Copyright (C) 2006  LISI - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* OntoQL is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* OntoQL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with OntoQL.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lisi.ontoql.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * @author Stéphane JEAN
 */
public final class ArrayHelper {

	public static boolean contain(Object[] array, Object object) {
		return indexOf(array, object) != -1;
	}

	public static int indexOf(Object[] array, Object object) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(object))
				return i;
		}
		return -1;
	}

	public static Object[] remove(Object[] array, int index) {
		Object[] result = new Object[array.length - 1];
		for (int i = 0; i < result.length; i++) {
			if (i < index) {
				result[i] = array[i];
			} else if (i > index) {
				result[i] = array[i + 1];
			}

		}
		return result;
	}

	public static String[] toStringArray(Object[] objects) {
		int length = objects.length;
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = objects[i].toString();
		}
		return result;
	}

	public static Integer[] toIntegerArray(int[] objects) {
		int length = objects.length;
		Integer[] result = new Integer[length];
		for (int i = 0; i < length; i++) {
			result[i] = new Integer(objects[i]);
		}
		return result;
	}

	public static Float[] toFloatArray(float[] objects) {
		int length = objects.length;
		Float[] result = new Float[length];
		for (int i = 0; i < length; i++) {
			result[i] = new Float(objects[i]);
		}
		return result;
	}

	public static String[] fillArray(String value, int length) {
		String[] result = new String[length];
		Arrays.fill(result, value);
		return result;
	}

	public static int[] fillArray(int value, int length) {
		int[] result = new int[length];
		Arrays.fill(result, value);
		return result;
	}

	public static String[] toStringArray(Collection coll) {
		return (String[]) coll.toArray(EMPTY_STRING_ARRAY);
	}

	public static String[][] to2DStringArray(Collection coll) {
		return (String[][]) coll.toArray(new String[coll.size()][]);
	}

	public static int[][] to2DIntArray(Collection coll) {
		return (int[][]) coll.toArray(new int[coll.size()][]);
	}

	public static int[] toIntArray(Collection coll) {
		Iterator iter = coll.iterator();
		int[] arr = new int[coll.size()];
		int i = 0;
		while (iter.hasNext()) {
			arr[i++] = ((Integer) iter.next()).intValue();
		}
		return arr;
	}

	public static boolean[] toBooleanArray(Collection coll) {
		Iterator iter = coll.iterator();
		boolean[] arr = new boolean[coll.size()];
		int i = 0;
		while (iter.hasNext()) {
			arr[i++] = ((Boolean) iter.next()).booleanValue();
		}
		return arr;
	}

	public static Object[] typecast(Object[] array, Object[] to) {
		return java.util.Arrays.asList(array).toArray(to);
	}

	// Arrays.asList doesn't do primitive arrays
	public static List toList(Object array) {
		if (array instanceof Object[])
			return Arrays.asList((Object[]) array); // faster?
		int size = Array.getLength(array);
		ArrayList list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			list.add(Array.get(array, i));
		}
		return list;
	}

	// Arrays.asList doesn't do primitive arrays
	public static Vector toVector(Object array) {
		int size = Array.getLength(array);
		Vector vector = new Vector(size);
		for (int i = 0; i < size; i++) {
			vector.add(Array.get(array, i));
		}
		return vector;
	}

	public static Set toSet(Object array) {
		int size = Array.getLength(array);
		Set set = new HashSet(size);
		for (int i = 0; i < size; i++) {
			set.add(Array.get(array, i));
		}
		return set;
	}

	public static String[] slice(String[] strings, int begin, int length) {
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = strings[begin + i];
		}
		return result;
	}

	public static Object[] slice(Object[] objects, int begin, int length) {
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = objects[begin + i];
		}
		return result;
	}

	public static List toList(Iterator iter) {
		List list = new ArrayList();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	public static Object[] join(Object[] x, Object[] y) {
		Object[] result = new Object[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	public static Object[] merge(Object[] x, Object[] y) {
		Object[] result = new Object[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		int nbAdded = 0;
		for (int i = 0; i < y.length; i++) {
			if (!contain(x, y[i])) {
				result[nbAdded + x.length] = y[i];
				nbAdded++;
			}
		}
		result = slice(result, 0, x.length + nbAdded);
		return result;
	}

	public static String[] join(String[] x, String[] y) {
		String[] result = new String[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	public static int[] join(int[] x, int[] y) {
		int[] result = new int[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	public static boolean[] join(boolean[] x, boolean[] y) {
		boolean[] result = new boolean[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	private ArrayHelper() {
	}

	public static String toString(Object[] array) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i < array.length - 1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public static boolean isAllNegative(int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= 0)
				return false;
		}
		return true;
	}

	public static boolean isAllTrue(boolean[] array) {
		for (int i = 0; i < array.length; i++) {
			if (!array[i])
				return false;
		}
		return true;
	}

	public static int countTrue(boolean[] array) {
		int result = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i])
				result++;
		}
		return result;
	}

	public static boolean isAllFalse(boolean[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i])
				return false;
		}
		return true;
	}

	public static void addAll(Collection collection, Object[] array) {
		for (int i = 0; i < array.length; i++) {
			collection.add(array[i]);
		}
	}

	public static final String[] EMPTY_STRING_ARRAY = {};

	public static final int[] EMPTY_INT_ARRAY = {};

	public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};

	public static final Class[] EMPTY_CLASS_ARRAY = {};

	public static final Object[] EMPTY_OBJECT_ARRAY = {};

	public static int[] getBatchSizes(int maxBatchSize) {
		int batchSize = maxBatchSize;
		int n = 1;
		while (batchSize > 1) {
			batchSize = getNextBatchSize(batchSize);
			n++;
		}
		int[] result = new int[n];
		batchSize = maxBatchSize;
		for (int i = 0; i < n; i++) {
			result[i] = batchSize;
			batchSize = getNextBatchSize(batchSize);
		}
		return result;
	}

	private static int getNextBatchSize(int batchSize) {
		if (batchSize <= 10) {
			return batchSize - 1; // allow 9,8,7,6,5,4,3,2,1
		} else if (batchSize / 2 < 10) {
			return 10;
		} else {
			return batchSize / 2;
		}
	}

	private static int SEED = 23;

	private static int PRIME_NUMER = 37;

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(Object[] array) {
		int length = array.length;
		int seed = SEED;
		for (int index = 0; index < length; index++) {
			seed = hash(seed, array[index] == null ? 0 : array[index].hashCode());
		}
		return seed;
	}

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(char[] array) {
		int length = array.length;
		int seed = SEED;
		for (int index = 0; index < length; index++) {
			seed = hash(seed, (int) array[index]);
		}
		return seed;
	}

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(byte[] bytes) {
		int length = bytes.length;
		int seed = SEED;
		for (int index = 0; index < length; index++) {
			seed = hash(seed, (int) bytes[index]);
		}
		return seed;
	}

	private static int hash(int seed, int i) {
		return PRIME_NUMER * seed + i;
	}

	/**
	 * Compare 2 arrays only at the first level
	 */
	public static boolean isEquals(Object[] o1, Object[] o2) {
		if (o1 == o2)
			return true;
		if (o1 == null || o2 == null)
			return false;
		int length = o1.length;
		if (length != o2.length)
			return false;
		for (int index = 0; index < length; index++) {
			if (!o1[index].equals(o2[index]))
				return false;
		}
		return true;
	}

	/**
	 * Compare 2 arrays only at the first level
	 */
	public static boolean isEquals(boolean[] b1, boolean[] b2) {
		if (b1 == b2)
			return true;
		if (b1 == null || b2 == null)
			return false;
		int length = b1.length;
		if (length != b2.length)
			return false;
		for (int index = 0; index < length; index++) {
			if (!b1[index] == (b2[index]))
				return false;
		}
		return true;
	}

	/**
	 * Compare 2 arrays only at the first level
	 */
	public static boolean isEquals(char[] o1, char[] o2) {
		if (o1 == o2)
			return true;
		if (o1 == null || o2 == null)
			return false;
		int length = o1.length;
		if (length != o2.length)
			return false;
		for (int index = 0; index < length; index++) {
			if (!(o1[index] == o2[index]))
				return false;
		}
		return true;
	}

	/**
	 * Compare 2 arrays only at the first level
	 */
	public static boolean isEquals(byte[] b1, byte[] b2) {
		if (b1 == b2)
			return true;
		if (b1 == null || b2 == null)
			return false;
		int length = b1.length;
		if (length != b2.length)
			return false;
		for (int index = 0; index < length; index++) {
			if (!(b1[index] == b2[index]))
				return false;
		}
		return true;
	}
}
