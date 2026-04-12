# Inventory Management System

## Overview

This project is a Java console application for managing stock records in a small inventory. It supports product creation, editing, deletion, searching, and file-based persistence.

## Features

- Add, update, and delete products
- Search by product name or category
- View inventory in a tabular format
- Save data to `inventory.txt`
- Load saved data automatically when the application starts

## Project Structure

- `InventoryApp.java` - application entry point and menu flow
- `Inventory.java` - inventory operations and product collection management
- `Product.java` - product model
- `FileManager.java` - file loading and saving
- `run_app.bat` - compile and run the application
- `run_tests.bat` - compile and run the regression test

## Technologies

- Java
- Object-oriented programming
- File I/O
- Console-based user interaction

## Running the Application

```powershell
.\run_app.bat
```

## Running the Tests

```powershell
.\run_tests.bat
```

## Example Use Cases

- Record new products and their quantities
- Update pricing or category information
- Search for all products in a category
- Persist inventory data between sessions
