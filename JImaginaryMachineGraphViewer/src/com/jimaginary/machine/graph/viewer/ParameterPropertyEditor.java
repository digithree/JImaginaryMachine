/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.graph.params.BernoulliParamPropertyPanel;
import com.jimaginary.machine.graph.params.ConstantParamPropertyPanel;
import com.jimaginary.machine.graph.params.PoissonParamPropertyPanel;
import com.jimaginary.machine.graph.params.ProbTableParamPropertyPanel;
import com.jimaginary.machine.graph.params.ProbTableSlidersParamPropertyPanel;
import com.jimaginary.machine.math.MathFunction;
import com.jimaginary.machine.math.MathFunctionFactory;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.List;
import javax.swing.JLabel;
import org.openide.util.Exceptions;

/**
 *
 * @author simonkenny
 */
public class ParameterPropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        String str = (String)getValue();
        if(str == null) {
            return "No Function Set";
        }
        return str;
    }

    @Override
    public void setAsText(String s) {
        setValue( s );
    }
    
    @Override
    public Component getCustomEditor() {
        String []idxNames = null;
        List<GraphNode> graphNodes = GraphData.getGraph().getAllNodes();
        for( GraphNode graphNode : graphNodes ) {
            GraphNodeInfo info = graphNode.getInfo();
            for( int i = 0 ; i < info.getNumParameters() ; i++ ) {
                // possible error, assumes functions are unique! likely they're not
                if( info.getParameter(i).equals(getAsText()) ) { 
                    idxNames = info.getParameterIdxNames(i);
                    break;
                }
            }
            if( idxNames != null ) {
                break;
            }
        }
        System.out.println("creating mathFunc from: "+getAsText());
        MathFunction mathFunc = null;
        try {
            mathFunc = MathFunctionFactory.createByParse(getAsText());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        if( mathFunc != null ) {
            System.out.println("mathFunc name is "+mathFunc.getName());
            if( mathFunc.getName().equals("Poisson") ) {
                return new PoissonParamPropertyPanel(idxNames,mathFunc,this);
            } else if( mathFunc.getName().equals("Bernoulli") ) {
                return new BernoulliParamPropertyPanel(idxNames,mathFunc,this);
            } else if( mathFunc.getName().equals("Constant") ) {
                return new ConstantParamPropertyPanel(idxNames,mathFunc,this);
            } else if( mathFunc.getName().equals("ProbTable") ) {
                //return new ProbTableParamPropertyPanel(idxNames,mathFunc,this);
                return new ProbTableSlidersParamPropertyPanel(idxNames,mathFunc,this);
            }
        } else {
            System.out.println("mathFunc couldn't be created");
        }
        return new JLabel("Error, couldn't make editor!");
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    /*
    // property change listener
    private List listeners = Collections.synchronizedList(new LinkedList());

    @Override
    public void addPropertyChangeListener (PropertyChangeListener pcl) {
        listeners.add (pcl);
    }

    @Override
    public void removePropertyChangeListener (PropertyChangeListener pcl) {
        listeners.remove (pcl);
    }

    private void fire (String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for(PropertyChangeListener pcl : pcls) {
            pcl.propertyChange(new PropertyChangeEvent (this, propertyName, old, nue));
        }
    }
    */
}
