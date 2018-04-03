package com.jaly.touchscreenor;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.widget.EditText;

/**
 * 处理EditText撤销和重做操作
 * @author Administrator
 *
 */
public class EditOperation implements Parcelable, Serializable {
     
	private static final long serialVersionUID = 1L;
	
	private String src;
    private int srcStart;
    private int srcEnd;
    
    private String dst;
    private int dstStart;
    private int dstEnd;
    
    EditOperation setSrc(CharSequence src, int srcStart, int srcEnd) {
        this.src = src != null ? src.toString() : "";
        this.srcStart = srcStart;
        this.srcEnd = srcEnd;
        return this;
    }
    
    EditOperation setDst(CharSequence dst, int dstStart, int dstEnd) {
        this.dst = dst != null ? dst.toString() : "";
        this.dstStart = dstStart;
        this.dstEnd = dstEnd;
        return this;
    }
    
    void undo(EditText text) {
        Editable editable = text.getText();
        
        int idx = -1;
        if (dstEnd > 0) {
            editable.delete(dstStart, dstEnd);
            
            if (src == null) {
                idx = dstStart;
            }
        }
        if (src != null) {
            editable.insert(srcStart, src);
            idx = srcStart + src.length();
        }
        if (idx >= 0) {
            text.setSelection(idx);
        }
    }
    
    void redo(EditText text) {
        Editable editable = text.getText();
        
        int idx = -1;
        if (srcEnd > 0) {
            editable.delete(srcStart, srcEnd);
            if (dst == null) {
                idx = srcStart;
            }
        }
        if (dst != null) {
            editable.insert(dstStart, dst);
            idx = dstStart + dst.length();
        }
        if (idx >= 0) {
            text.setSelection(idx);
        }
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.src);
        dest.writeInt(this.srcStart);
        dest.writeInt(this.srcEnd);
        dest.writeString(this.dst);
        dest.writeInt(this.dstStart);
        dest.writeInt(this.dstEnd);
    }
    
    EditOperation() {
    }
    
    EditOperation(Parcel in) {
        this.src = in.readString();
        this.srcStart = in.readInt();
        this.srcEnd = in.readInt();
        this.dst = in.readString();
        this.dstStart = in.readInt();
        this.dstEnd = in.readInt();
    }
    
    public static final Creator<EditOperation> CREATOR = new Creator<EditOperation>() {
        @Override
        public EditOperation createFromParcel(Parcel source) {
            return new EditOperation(source);
        }
        
        @Override
        public EditOperation[] newArray(int size) {
            return new EditOperation[size];
        }
    };
}
