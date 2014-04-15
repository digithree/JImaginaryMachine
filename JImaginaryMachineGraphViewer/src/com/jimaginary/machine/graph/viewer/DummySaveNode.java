/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author simonkenny
 */
public class DummySaveNode extends AbstractNode {
    SaveCookieImpl impl;

    public DummySaveNode(Children children) {
        super(children);
        impl = new SaveCookieImpl();
    }

    public DummySaveNode() {
        super(Children.LEAF);
        impl = new SaveCookieImpl();
    }

    public void fire(boolean modified) {
        if (modified) {
            //If the text is modified,
            //we implement SaveCookie,
            //and add the implementation to the cookieset:
            getCookieSet().assign(SaveCookie.class, impl);
        } else {
            //Otherwise, we make no assignment
            //and the SaveCookie is not made available:
            getCookieSet().assign(SaveCookie.class);
        }
    }

    private class SaveCookieImpl implements SaveCookie {

        public void save() throws IOException {

            NotifyDescriptor.Confirmation msg = new NotifyDescriptor.Confirmation("Do you want to save Graph?",
                    NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(msg);

            //When user clicks "Yes", indicating they really want to save,
            //we need to disable the Save button and Save menu item,
            //so that it will only be usable when the next change is made
            //to the text field:
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                fire(false);
                //Implement your save functionality here.
                System.out.println("DUMMY SAVE OPERATION");
            }

        }
    }
}