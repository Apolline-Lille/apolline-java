package org.apolline.cmd;

import java.util.List;

public interface Ordonnanceur {
   public void execute();
   public String[] getValues();
   public boolean haveValues();
   public void read();
}
