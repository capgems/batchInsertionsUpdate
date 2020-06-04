package com.dbtesting.database;

import org.springframework.batch.item.ItemProcessor;

import com.dbtesting.model.VillageMaster;

public class VillageProcessor implements ItemProcessor<VillageMaster, VillageMaster> {

 @Override
 public VillageMaster process(VillageMaster villageMaster) throws Exception {
  return villageMaster;
 }

} 
