package org.atreus.core.ext.plugins;

import org.atreus.core.AtreusManager;

/**
 * Atreus Plugin Interface.
 *
 * @author Martin Crawford
 */
public interface AtreusPlugin {

  public void init();

  public void preMapping(AtreusManager manager);

  public void postMapping(AtreusManager manager);

  public void destroy();

}