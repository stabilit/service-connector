package com.stabilit.jna;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {

	public static class MEMORYSTATUS extends Structure {
		public int dwLength;
		public int dwMemoryLoad;
		public NativeLong dwTotalPhys;
		public NativeLong dwAvailPhys;
		public NativeLong dwTotalPageFile;
		public NativeLong dwAvailPageFile;
		public NativeLong dwTotalVirtual;
		public NativeLong dwAvailVirtual;
		public NativeLong ullAvailExtendedVirtual;

	} // End of MEMORYSTATUS

	void GlobalMemoryStatus(MEMORYSTATUS result);

	public static class SYSTEM_INFO extends Structure {

		public arch1 arch1;
		public int dwPageSize;
		public Pointer lpMinimumApplicationAddress;
		public Pointer lpMaximumApplicationAddress;
		public int dwActiveProcessorMask;
		public int dwNumberOfProcessors;
		public int dwProcessorType;
		public int dwAllocationGranularity;
		public short wProcessorLevel;
		public short wProcessorRevision;
	}

	public static class arch1 extends Union {
		public int dwOemId;
		public arch2 arch2;
	}

	public static class arch2 extends Structure {
		public short wProcessorArchitecture;
		public short wReserved;
	}

	void GetNativeSystemInfo(SYSTEM_INFO result);
}
