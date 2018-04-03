package com.jaly.touchscreenor;

import java.util.LinkedList;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * EditText撤销和重做管理器（单例模式）
 * @author Administrator
 *
 */
public class OperationManager implements TextWatcher {   
    
    private final EditText editText;
    private EditOperation opt;
    private boolean enable = true; 
    
    private final LinkedList<EditOperation> undoOpts = new LinkedList<EditOperation>();
    private final LinkedList<EditOperation> redoOpts = new LinkedList<EditOperation>();
    
    private OperationManager(EditText editText) {
        this.editText = editText;
        editText.addTextChangedListener(this);
    }
    
    public static OperationManager setup(EditText editText) {
    	OperationManager mgr = new OperationManager(editText);
    	editText.addTextChangedListener(mgr);
    	return mgr;
    }    
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count > 0) {
            int end = start + count;
            if (enable) {
                if (opt == null) {
                    opt = new EditOperation();
                }
                opt.setSrc(s.subSequence(start, end), start, end);
            }
        }
    }
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0) {
            int end = start + count;
            if (enable) {
                if (opt == null) {
                    opt = new EditOperation();
                }
                opt.setDst(s.subSequence(start, end), start, end);
            }
        }
    }
    
    @Override
    public void afterTextChanged(Editable s) {
        if (enable && opt != null) {
            if (!redoOpts.isEmpty()) {
                redoOpts.clear();
            } 
            undoOpts.push(opt);
        }
        opt = null;
    }
    
    public boolean canUndo() {
        return !undoOpts.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoOpts.isEmpty();
    }
    
    public boolean undo() {
        if (canUndo()) {
            EditOperation undoOpt = undoOpts.pop(); 
            //屏蔽撤销产生的事件
            enable = false;
            undoOpt.undo(editText);
            enable = true; 
            //填入重做栈
            redoOpts.push(undoOpt);
            return true;
        }
        return false;
    }
    
    public boolean redo() {
        if (canRedo()) {
            EditOperation redoOpt = redoOpts.pop(); 
            //屏蔽重做产生的事件
            enable = false;
            redoOpt.redo(editText);
            enable = true; 
            //填入撤销
            undoOpts.push(redoOpt);
            return true;
        }
        return false;
    } 
    
}
