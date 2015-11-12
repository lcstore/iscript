package com.taobao.securityjni.tools;

public class DataContext
{
    public int category;
    public Object extData;
    public int index;
    public int type;

    public DataContext()
    {
        this.index = -1;
        this.extData = null;
        this.category = -1;
        this.type = -1;
    }

    public DataContext(int paramInt, Object paramObject)
    {
        this.index = paramInt;
        this.extData = paramObject;
    }

    public DataContext(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    {
        this.index = paramInt1;
        this.extData = paramObject;
        this.category = paramInt2;
        this.type = paramInt3;
    }
}
