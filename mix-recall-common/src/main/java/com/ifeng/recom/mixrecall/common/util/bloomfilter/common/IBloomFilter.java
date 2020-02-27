package com.ifeng.recom.mixrecall.common.util.bloomfilter.common;

import java.nio.ByteBuffer;

import org.apache.hadoop.io.RawComparator;

/**
 * Created by jibin on 2017/7/4.
 */
public interface IBloomFilter{
    long getKeyCount();

    long getMaxKeys();

    long getByteSize();

    byte[] createBloomKey(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6);

    RawComparator<byte[]> getComparator();
    boolean contains(byte[] var1, int var2, int var3, ByteBuffer var4);

    boolean supportsAutoLoading();
}
