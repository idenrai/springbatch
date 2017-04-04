package com.test.batch;

import org.springframework.batch.item.ItemProcessor;

/*
 * 取得したアイテムを加工（ここでは「fruit_price.csv」）
 */
public class FruitItemProcessor implements ItemProcessor<Fruit, Fruit> {

  @Override
  public Fruit process(final Fruit fruit) throws Exception {
    final String title = fruit.getName().toUpperCase();
    final int price = fruit.getPrice();

    final Fruit transformColumns = new Fruit(title, price);

    return transformColumns;
  }

}
