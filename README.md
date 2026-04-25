# DeliveryAPP - Online Food Delivery System
### Single-File Java Console Application

---

## How to Run

### Step 1 - Compile
```
javac Main.java
```

### Step 2 - Run
```
java Main
```

That's it. No setup, no folders, no flags needed.

---

### Run in VS Code
1. Open `Main.java`
2. Install "Extension Pack for Java" (first time only)
3. Click the [Run Java] button at the top right

### Run in IntelliJ IDEA
1. Open `Main.java`
2. Press Shift + F10  or click the green Play button

### Run in CMD / Terminal
```
cd path\to\folder
javac Main.java
java Main
```

> Requires JDK 8 or higher. No external libraries needed.

---

## Why does this work as one file?

Java allows multiple classes in a single `.java` file as long as only ONE class
is declared `public` - and that must match the filename.

In this project:
- `Main` is the only `public` class  ->  file is named `Main.java`
- All other classes (Order, User, MenuItem, etc.) have no `public` keyword
- This compiles perfectly with just: `javac Main.java`

---

## Project Structure (inside Main.java)

```
Main.java
|
+-- abstract class Order          <- base class, abstract calculateBill()
|     +-- class RegularOrder      <- full price + 18% GST
|     +-- class PremiumOrder      <- 20% off + 18% GST
|
+-- class User                    <- customer entity
+-- class MenuItem                <- single menu item
+-- class Restaurant              <- holds ArrayList of MenuItems
+-- public class Main             <- entry point + all features
```

---

## OOP Concepts Used

### 1. Abstract Class
```java
abstract class Order {
    public abstract double calculateBill(); // subclasses must implement
}
```

### 2. Inheritance
```java
class RegularOrder extends Order { ... }
class PremiumOrder extends Order { ... }
```

### 3. Polymorphism (Runtime Dispatch)
```java
Order r = new RegularOrder("O1", 150, "U1", "Spice Garden");
Order p = new PremiumOrder("O2", 150, "U1", "Spice Garden");

r.calculateBill(); // calls RegularOrder version -> Rs.224.20
p.calculateBill(); // calls PremiumOrder version -> Rs.188.80
// same method call on Order type, different result = POLYMORPHISM
```

### 4. Encapsulation
```java
private String userId;
public String getUserId() { return userId; } // controlled access
```

### 5. Static Field
```java
public static double deliveryCharge = 40.0; // shared by all orders
```

### 6. Collections (no database)
```java
static ArrayList<Order>      orders      = new ArrayList<>();
static HashMap<String, User> users       = new HashMap<>();
static ArrayList<Restaurant> restaurants = new ArrayList<>();
```

---

## Bill Calculation

```
Regular Order:
  Bill = (baseAmount + 40) x 1.18
  Example (base = Rs.150):  (150 + 40) x 1.18 = Rs.224.20

Premium Order:
  Bill = (baseAmount x 0.8 + 40) x 1.18
  Example (base = Rs.150):  (120 + 40) x 1.18 = Rs.188.80

Coupon SAVE10:
  Final = bill x 0.90  (10% off)
```

---

## Menu (Pre-loaded Sample Data)

### Spice Garden (R001)

| Item ID | Item Name          | Price  |
|---------|--------------------|--------|
| SG01    | Butter Chicken     | Rs.280 |
| SG02    | Paneer Tikka       | Rs.220 |
| SG03    | Garlic Naan        | Rs.50  |
| SG04    | Dal Makhani        | Rs.180 |
| SG05    | Gulab Jamun (2 pc) | Rs.80  |

### Pizza Palace (R002)

| Item ID | Item Name             | Price  |
|---------|-----------------------|--------|
| PP01    | Margherita Pizza (M)  | Rs.299 |
| PP02    | BBQ Chicken Pizza (M) | Rs.349 |
| PP03    | Garlic Bread (4 pcs)  | Rs.99  |
| PP04    | Pasta Arrabbiata      | Rs.199 |
| PP05    | Chocolate Lava Cake   | Rs.149 |

Pre-loaded user: **U001** / Aarav Sharma  <- use this to skip registration during testing

---

## Quick Test Walkthrough

```
1. App starts -> Polymorphism demo prints automatically

2. Press 1 -> Register yourself -> get User ID (e.g. U002)

3. Press 2 -> View restaurants -> note down item IDs

4. Press 3 -> Place Order
     - Enter User ID: U002
     - Select restaurant: 1
     - Add items: SG01 then SG03 then done
     - Choose type: 1 (Regular) or 2 (Premium)
     - Coupon: SAVE10  or press Enter to skip
     - Receipt prints with final bill

5. Press 4 -> View My Orders -> enter U002

6. Press 5 -> Track order -> enter Order ID (e.g. ORD0001)
           -> choose to update status to DELIVERED
```

---

## Coupon

| Code   | Discount        |
|--------|-----------------|
| SAVE10 | 10% off final bill |

---

## Common Errors and Fixes

### Error: unmappable character for encoding windows-1252
This happens when the file has special Unicode symbols.
This version uses plain ASCII only so this error will NOT occur.
Just run: `javac Main.java`

### Error: javac not found
Java is not installed or not in PATH.
Download JDK from: https://www.oracle.com/java/technologies/downloads/
Then add it to your system PATH.

### Error: class Main is public, should be in a file named Main.java
Make sure the file is saved as exactly `Main.java` (capital M).
