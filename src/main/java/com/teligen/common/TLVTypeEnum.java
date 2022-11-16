package com.teligen.common;

public enum TLVTypeEnum {

    BOOLEAN("BOOLEAN",0),
    BYTE("BYTE",1),
    SHORT("SHORT",2),
    INTEGER("INTEGER",3),
    LONG("LONG",4),
    FLOAT("FLOAT",5),
    DOUBLE("DOUBLE",6),
    CHAR("CHAR",7),
    STRING("STRING",8),
    BYTEARR("BYTEARR",9),
    INNER("INNER",10);
    private String typeName;
    private int typeint;

    TLVTypeEnum(String typeName, int typeint) {
        this.typeName = typeName;
        this.typeint = typeint;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeint() {
        return typeint;
    }

    public void setTypeint(int typeint) {
        this.typeint = typeint;
    }
}
