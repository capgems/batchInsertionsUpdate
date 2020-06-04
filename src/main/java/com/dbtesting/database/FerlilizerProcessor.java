package com.dbtesting.database;

import org.springframework.batch.item.ItemProcessor;

import com.dbtesting.model.Fertilizers;

public class FerlilizerProcessor implements ItemProcessor<Fertilizers, Fertilizers> {

	 @Override
	 public Fertilizers process(Fertilizers fertlilizers) throws Exception {
	  return fertlilizers;
	 }

	} 
