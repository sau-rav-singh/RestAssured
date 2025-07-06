@NextGenReadData
Feature: Read Data from Excel

  Scenario Outline: TS_01_Place an order
    Given A Workbook named "secTypesDemo" and sheetname as"equity" is read and to write Data
    When Read symbol as "<SYMBOL>" and orderType as "<ORDERTYPE>" and print values

    Examples:
      | SYMBOL | ORDERTYPE |
      | SYMBOL | ORDERTYPE |
      | SYMBOL | ORDERTYPE |

  Scenario Outline: TS_02_Place an order
    Given A Workbook named "secTypesDemo" and sheetname as"equity" is read and to write Data
    When Read symbol as "<SYMBOL>" and orderType as "<ORDERTYPE>" and print values

    Examples:
      | SYMBOL | ORDERTYPE |
      | SYMBOL | ORDERTYPE |