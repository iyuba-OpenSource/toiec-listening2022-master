package com.iyuba.core.me.goldvip;

import android.content.Context;
import android.content.res.XmlResourceParser;
import androidx.annotation.NonNull;
import androidx.annotation.XmlRes;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuyVIPParser {
    private static final String ITEM_TAG = "item";
    private static final int AVG_NUMBER_OF_ITEMS = 9;
    private static final int RESOURCE_NOT_FOUND = 0;

    private final Context mContext;

    public BuyVIPParser(@NonNull Context context) {
        mContext = context;
    }

    public List<BuyVIPItem> parse(@XmlRes int xmlResId) {
        XmlResourceParser parser = mContext.getResources().getXml(xmlResId);
        List<BuyVIPItem> items = new ArrayList<>(AVG_NUMBER_OF_ITEMS);
        int eventType;
        try {
            do {
                eventType = parser.next();
                if (eventType == XmlResourceParser.START_TAG && ITEM_TAG.equals(parser.getName())) {
                    items.add(parseNewItem(parser));
                }
            } while (eventType != XmlResourceParser.END_DOCUMENT);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return items;
    }

    private BuyVIPItem parseNewItem(@NonNull XmlResourceParser parser) {
        BuyVIPItem item = new BuyVIPItem();

        final int numberOfAttributes = parser.getAttributeCount();
        for (int i = 0; i < numberOfAttributes; i += 1) {
            String attributeName = parser.getAttributeName(i);
            switch (attributeName) {
                case ItemAttribute.NAME:
                    int titleResource = parser.getAttributeResourceValue(i, RESOURCE_NOT_FOUND);
                    item.name = titleResource == RESOURCE_NOT_FOUND
                            ? parser.getAttributeValue(i) : mContext.getString(titleResource);
                    break;
                case ItemAttribute.MONTH:
                    item.month = parser.getAttributeIntValue(i, 1);
                    break;
                case ItemAttribute.PRICE:
                    item.price = parser.getAttributeIntValue(i, 10);
                    break;
                case ItemAttribute.PRODUCT_ID: {
                    int productIdResource = parser.getAttributeResourceValue(i, RESOURCE_NOT_FOUND);
                    item.productId = productIdResource == RESOURCE_NOT_FOUND ?
                            parser.getAttributeIntValue(i, 10) : mContext.getResources().getInteger(productIdResource);
                    break;
                }
            }
        }
        return item;
    }

    private interface ItemAttribute {
        String NAME = "name";
        String MONTH = "month";
        String PRICE = "price";
        String PRODUCT_ID = "productId";
    }
}
