package org.serviceconnector.cache;

import java.util.Comparator;

import org.serviceconnector.Constants;

/**
 * The Class CacheKeyComparator. The key comparator contains knowledge of sorting the keys. It compares serviceName + cacheId.
 * If both are the same value partNr is considered.
 */
public class CacheIdComparator implements Comparator<String> {

	@Override
	public int compare(String cid1, String cid2) {

		String metaEntryCid1 = cid1.substring(0, cid1.lastIndexOf(Constants.SLASH));
		String metaEntryCid2 = cid2.substring(0, cid2.lastIndexOf(Constants.SLASH));
		int stringResult = metaEntryCid1.compareTo(metaEntryCid2);
		if (stringResult != 0) {
			// // meta entries are not equal, return
			return stringResult;
		}

		String appendixNr1 = cid1.substring(cid1.lastIndexOf(Constants.SLASH) + 1, cid1.lastIndexOf(Constants.PIPE));
		String appendixNr2 = cid2.substring(cid2.lastIndexOf(Constants.SLASH) + 1, cid2.lastIndexOf(Constants.PIPE));
		int appendixNr1Int = new Integer(appendixNr1);
		int appendixNr2Int = new Integer(appendixNr2);
		if (appendixNr1Int > appendixNr2Int) {
			return 1;
		} else if (appendixNr1Int < appendixNr2Int) {
			return -1;
		}

		String partNr1 = cid1.substring(cid1.lastIndexOf(Constants.PIPE) + 1);
		String partNr2 = cid2.substring(cid2.lastIndexOf(Constants.PIPE) + 1);
		int partNr1Int = new Integer(partNr1);
		int partNr2Int = new Integer(partNr2);

		if (partNr1Int == partNr2Int)
			return 0;
		if (partNr1Int > partNr2Int)
			return 1;
		return -1;
	}
}
