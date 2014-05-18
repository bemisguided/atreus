package org.atreus.core.ext.plugins;

import org.atreus.core.AtreusManager;

import java.util.Map;

/**
 * Atreus Plugin Interface.
 *
 * @author Martin Crawford
 */
public interface AtreusPlugin {

  public void init(Map<String, Object> configuration);

  public void preMapping(AtreusManager manager);

  public void postMapping(AtreusManager manager);

  public void destroy();

}