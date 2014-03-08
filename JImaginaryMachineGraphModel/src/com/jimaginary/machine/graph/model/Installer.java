/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.model;

import com.jimaginary.machine.api.Graph;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
    Graph graph;

    @Override
    public void restored() {
        graph = null;
    }

}
