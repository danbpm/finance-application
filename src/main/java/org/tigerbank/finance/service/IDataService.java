package org.tigerbank.finance.service;

import java.io.IOException;

public interface IDataService {
    void exportToFile(String filename) throws IOException;
    void importFromFile(String filename) throws IOException;
}