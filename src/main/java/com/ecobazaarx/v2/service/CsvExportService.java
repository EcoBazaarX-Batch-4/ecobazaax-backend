package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.model.Order;
import com.ecobazaarx.v2.model.OrderItem;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class CsvExportService {
    public String writeOrdersToCsv(List<Order> orders) {
        StringWriter stringWriter = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(stringWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {
                    "Order ID", "Order Date", "Status", "Customer Email", "Product ID",
                    "Product Name", "Quantity", "Price Per Item", "Carbon Per Item",
                    "Shipping Cost", "Tax Amount", "Discount Amount", "Total Carbon"
            };
            csvWriter.writeNext(header);

            for (Order order : orders) {
                for (OrderItem item : order.getOrderItems()) {
                    String[] row = {
                            order.getId().toString(),
                            order.getOrderDate().toString(),
                            order.getStatus().name(),
                            order.getUser().getEmail(),
                            item.getProduct().getId().toString(),
                            item.getProductName(),
                            String.valueOf(item.getQuantity()),
                            item.getPricePerItem().toString(),
                            item.getCarbonFootprintPerItem().toString(),
                            order.getShippingCost().toString(),
                            order.getTaxAmount().toString(),
                            order.getDiscountAmount() != null ? order.getDiscountAmount().toString() : "0.00",
                            order.getTotalCarbonFootprint().toString()
                    };
                    csvWriter.writeNext(row);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing CSV export", e);
        }

        return stringWriter.toString();
    }
}
