import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// =============================================================================
//  Online Food Delivery App  -  DeliveryAPP
//  
//  HOW TO RUN (pick one):
//
//  OPTION A - VS Code (RECOMMENDED):
//    1. Press Ctrl+Shift+P
//    2. Type "Run in Terminal" and select it
//    OR open settings.json and add:
//       "code-runner.runInTerminal": true
//
//  OPTION B - CMD / PowerShell:
//    javac Main.java
//    java Main
//
//  OPTION C - VS Code Terminal (bottom panel):
//    javac Main.java
//    java Main
// =============================================================================


// -----------------------------------------------------------------------------
//  ABSTRACT BASE CLASS : Order
// -----------------------------------------------------------------------------
abstract class Order {

    private String orderId;
    private double baseAmount;
    private String status;
    private String userId;
    private String restaurantName;

    public static double deliveryCharge = 40.0;

    public Order(String orderId, double baseAmount, String userId, String restaurantName) {
        this.orderId        = orderId;
        this.baseAmount     = baseAmount;
        this.userId         = userId;
        this.restaurantName = restaurantName;
        this.status         = "PLACED";
    }

    public abstract double calculateBill();

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("  [OK] Status -> " + newStatus);
    }

    public String getOrderId()        { return orderId; }
    public double getBaseAmount()     { return baseAmount; }
    public String getStatus()         { return status; }
    public String getUserId()         { return userId; }
    public String getRestaurantName() { return restaurantName; }

    public String toString() {
        return String.format(
            "  ID:%-8s | Shop:%-18s | Base:Rs.%-7.2f | Bill:Rs.%-7.2f | %s",
            orderId, restaurantName, baseAmount, calculateBill(), status);
    }
}


// -----------------------------------------------------------------------------
//  RegularOrder : Bill = (base + 40) x 1.18
//  Example base=150 -> Rs.224.20
// -----------------------------------------------------------------------------
class RegularOrder extends Order {

    private static final double TAX = 0.18;

    public RegularOrder(String id, double base, String uid, String shop) {
        super(id, base, uid, shop);
    }

    public double calculateBill() {
        double sub = getBaseAmount() + Order.deliveryCharge;
        return Math.round(sub * (1 + TAX) * 100.0) / 100.0;
    }

    public String toString() {
        double sub = getBaseAmount() + Order.deliveryCharge;
        double tax = sub * TAX;
        return super.toString() + "\n" +
            String.format("  [REGULAR] Rs.%.2f + Rs.%.2f delivery + Rs.%.2f tax = Rs.%.2f",
                getBaseAmount(), Order.deliveryCharge, tax, calculateBill());
    }
}


// -----------------------------------------------------------------------------
//  PremiumOrder : Bill = (base*0.8 + 40) x 1.18
//  Example base=150 -> Rs.188.80
// -----------------------------------------------------------------------------
class PremiumOrder extends Order {

    private static final double DISC = 0.20;
    private static final double TAX  = 0.18;

    public PremiumOrder(String id, double base, String uid, String shop) {
        super(id, base, uid, shop);
    }

    public double calculateBill() {
        double disc = getBaseAmount() * (1 - DISC);
        double sub  = disc + Order.deliveryCharge;
        return Math.round(sub * (1 + TAX) * 100.0) / 100.0;
    }

    public String toString() {
        double disc = getBaseAmount() * (1 - DISC);
        double sub  = disc + Order.deliveryCharge;
        double tax  = sub * TAX;
        return super.toString() + "\n" +
            String.format("  [PREMIUM] Rs.%.2f-20%%=Rs.%.2f + Rs.%.2f delivery + Rs.%.2f tax = Rs.%.2f",
                getBaseAmount(), disc, Order.deliveryCharge, tax, calculateBill());
    }
}


// -----------------------------------------------------------------------------
//  User
// -----------------------------------------------------------------------------
class User {
    private String userId, name, phone, address;

    public User(String userId, String name, String phone, String address) {
        this.userId = userId; this.name = name;
        this.phone  = phone;  this.address = address;
    }

    public String getUserId()  { return userId; }
    public String getName()    { return name; }
    public String getPhone()   { return phone; }
    public String getAddress() { return address; }

    public String toString() {
        return "User[" + userId + "] " + name + " | " + phone + " | " + address;
    }
}


// -----------------------------------------------------------------------------
//  MenuItem
// -----------------------------------------------------------------------------
class MenuItem {
    private String itemId, name;
    private double price;

    public MenuItem(String itemId, String name, double price) {
        this.itemId = itemId; this.name = name; this.price = price;
    }

    public String getItemId() { return itemId; }
    public String getName()   { return name; }
    public double getPrice()  { return price; }

    public String toString() {
        return String.format("    [%-4s]  %-26s  Rs.%.2f", itemId, name, price);
    }
}


// -----------------------------------------------------------------------------
//  Restaurant
// -----------------------------------------------------------------------------
class Restaurant {
    private String restId, name;
    private ArrayList<MenuItem> menu = new ArrayList<MenuItem>();

    public Restaurant(String restId, String name) {
        this.restId = restId; this.name = name;
    }

    public void addMenuItem(MenuItem m) { menu.add(m); }

    public void displayMenu() {
        System.out.println();
        System.out.println("  +------------------------------------------+");
        System.out.println("  |  " + name + " - Menu");
        System.out.println("  +------------------------------------------+");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println(menu.get(i));
        }
        System.out.println("  +------------------------------------------+");
    }

    public MenuItem findItem(String id) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.get(i).getItemId().equalsIgnoreCase(id)) return menu.get(i);
        }
        return null;
    }

    public String getRestId() { return restId; }
    public String getName()   { return name; }
    public int    getSize()   { return menu.size(); }

    public String toString() {
        return "[" + restId + "] " + name + "  (" + menu.size() + " items)";
    }
}


// -----------------------------------------------------------------------------
//  MAIN CLASS
// -----------------------------------------------------------------------------
public class Main {

    static ArrayList<Order>      orders      = new ArrayList<Order>();
    static HashMap<String, User> users       = new HashMap<String, User>();
    static ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

    static int userCounter  = 2;
    static int orderCounter = 1;

    static final String COUPON     = "SAVE10";
    static final double COUPON_OFF = 0.10;

    // Using System.console() first, fallback to Scanner
    // This guarantees interactive input works in ALL environments
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        // Safety check - if stdin is not interactive, warn and exit
        if (System.console() == null && !hasInteractiveInput()) {
            System.out.println("========================================================");
            System.out.println("  ERROR: This app needs interactive terminal input.");
            System.out.println("  VS Code Code Runner does NOT support input by default.");
            System.out.println();
            System.out.println("  HOW TO FIX (choose one):");
            System.out.println();
            System.out.println("  FIX 1 - Use VS Code Terminal (EASIEST):");
            System.out.println("    1. Press Ctrl + ` (backtick) to open terminal");
            System.out.println("    2. Type: javac Main.java");
            System.out.println("    3. Type: java Main");
            System.out.println();
            System.out.println("  FIX 2 - Configure Code Runner for terminal:");
            System.out.println("    1. Press Ctrl + Shift + P");
            System.out.println("    2. Search: Open User Settings (JSON)");
            System.out.println("    3. Add this line inside the { }:");
            System.out.println("       \"code-runner.runInTerminal\": true");
            System.out.println("    4. Save and run again");
            System.out.println();
            System.out.println("  FIX 3 - CMD/PowerShell:");
            System.out.println("    cd to your folder, then:");
            System.out.println("    javac Main.java && java Main");
            System.out.println("========================================================");
            return;
        }

        loadSampleData();
        polymorphismDemo();

        boolean running = true;
        while (running) {
            showMenu();
            int choice = nextInt("Choice");
            switch (choice) {
                case 1: registerUser();    break;
                case 2: viewRestaurants(); break;
                case 3: placeOrder();      break;
                case 4: viewMyOrders();    break;
                case 5: trackOrder();      break;
                case 6:
                    System.out.println("\n  Thank you! Goodbye!\n");
                    running = false;
                    break;
                default:
                    System.out.println("  [!] Enter a number 1-6.");
            }
        }
    }

    // ------------------------------------------------------------------
    //  Check if we can actually read input (stdin is not closed/piped)
    // ------------------------------------------------------------------
    static boolean hasInteractiveInput() {
        try {
            // Try to see if there are bytes available - if 0 and not ready,
            // we're likely in a non-interactive mode (Code Runner output panel)
            return System.in.available() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------------------------
    //  1. Register User
    // ------------------------------------------------------------------
    static void registerUser() {
        System.out.println("\n  ===== Register New User =====");

        System.out.print("  Name    : ");
        String name = nextLine();

        System.out.print("  Phone   : ");
        String phone = nextLine();

        System.out.print("  Address : ");
        String address = nextLine();

        if (name.isEmpty()) {
            System.out.println("  [!] Name cannot be empty.");
            return;
        }

        String uid = "U" + String.format("%03d", userCounter++);
        users.put(uid, new User(uid, name, phone, address));

        System.out.println();
        System.out.println("  [OK] Registered successfully!");
        System.out.println("  >>> Your User ID : " + uid);
        System.out.println("  >>> Keep this ID to place orders!");
    }

    // ------------------------------------------------------------------
    //  2. View Restaurants
    // ------------------------------------------------------------------
    static void viewRestaurants() {
        System.out.println("\n  ===== Restaurants =====");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + restaurants.get(i));
        }
        System.out.print("\n  Enter number to see menu (0 = back): ");
        int pick = nextInt("number");

        if (pick >= 1 && pick <= restaurants.size()) {
            restaurants.get(pick - 1).displayMenu();
        }
    }

    // ------------------------------------------------------------------
    //  3. Place Order
    // ------------------------------------------------------------------
    static void placeOrder() {
        System.out.println("\n  ===== Place Order =====");

        // Verify user
        System.out.print("  Your User ID: ");
        String uid = nextLine();
        if (!users.containsKey(uid)) {
            System.out.println("  [!] User ID '" + uid + "' not found.");
            System.out.println("  [!] Please register first using option 1.");
            return;
        }
        System.out.println("  Welcome, " + users.get(uid).getName() + "!");

        // Choose restaurant
        System.out.println("\n  --- Choose Restaurant ---");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + restaurants.get(i));
        }
        System.out.print("  Restaurant number: ");
        int rNum = nextInt("number");

        if (rNum < 1 || rNum > restaurants.size()) {
            System.out.println("  [!] Invalid restaurant number.");
            return;
        }
        Restaurant rest = restaurants.get(rNum - 1);
        rest.displayMenu();

        // Add items
        double total = 0.0;
        System.out.println("\n  --- Add Items to Cart ---");
        System.out.println("  (Type item ID and press Enter. Type 'done' to finish.)");

        while (true) {
            System.out.print("  Item ID: ");
            String input = nextLine();

            if (input.equalsIgnoreCase("done")) break;

            if (input.isEmpty()) {
                System.out.println("  [!] Please type an item ID or 'done'.");
                continue;
            }

            MenuItem found = rest.findItem(input);
            if (found == null) {
                System.out.println("  [!] '" + input + "' not found. Check the menu above.");
            } else {
                total += found.getPrice();
                System.out.println("  [+] Added: " + found.getName() +
                    "  Rs." + found.getPrice() +
                    "  |  Cart: Rs." + String.format("%.2f", total));
            }
        }

        if (total == 0.0) {
            System.out.println("  [!] Cart is empty. Order cancelled.");
            return;
        }

        // Order type
        System.out.println("\n  --- Order Type ---");
        System.out.println("  1. Regular  (base + Rs.40 delivery + 18% GST)");
        System.out.println("  2. Premium  (20% off base + Rs.40 delivery + 18% GST)");
        System.out.print("  Choose 1 or 2: ");
        int type = nextInt("type");
        if (type != 2) type = 1;

        // Coupon
        System.out.print("  Coupon code (Enter to skip): ");
        String cpn = nextLine();
        boolean hasCoupon = cpn.equalsIgnoreCase(COUPON);
        if (!cpn.isEmpty() && !hasCoupon) {
            System.out.println("  [!] Coupon not recognised.");
        }
        if (hasCoupon) {
            System.out.println("  [OK] Coupon SAVE10 applied - 10% off!");
        }

        // Create order -> POLYMORPHISM
        String oid = "ORD" + String.format("%04d", orderCounter++);
        Order order = (type == 2)
            ? new PremiumOrder(oid, total, uid, rest.getName())
            : new RegularOrder(oid, total, uid, rest.getName());

        orders.add(order);

        double bill      = order.calculateBill();   // polymorphic call
        double finalBill = hasCoupon
            ? Math.round(bill * (1 - COUPON_OFF) * 100.0) / 100.0
            : bill;

        String[] riders = {"Raju (4.8*)", "Priya (4.9*)", "Arjun (4.7*)", "Meera (4.6*)"};
        order.updateStatus("RIDER: " + riders[(int)(Math.random() * riders.length)]);

        // Print receipt
        System.out.println();
        System.out.println("  +--------------------------------------+");
        System.out.println("  |            ORDER RECEIPT             |");
        System.out.println("  +--------------------------------------+");
        System.out.println("  |  Order ID   : " + padRight(oid, 22) + "|");
        System.out.println("  |  Restaurant : " + padRight(rest.getName(), 22) + "|");
        System.out.println("  |  Type       : " + padRight(type==2?"PREMIUM":"REGULAR", 22) + "|");
        System.out.printf ("  |  Items Total: Rs. %-19.2f|%n", total);
        System.out.printf ("  |  Tax + Deliv: Rs. %-19.2f|%n", bill);
        if (hasCoupon)
        System.out.printf ("  |  SAVE10 -10%%: -Rs.%-19.2f|%n", bill - finalBill);
        System.out.println("  |--------------------------------------|");
        System.out.printf ("  |  FINAL BILL : Rs. %-19.2f|%n", finalBill);
        System.out.println("  +--------------------------------------+");
        System.out.println("  Note your Order ID: " + oid);
    }

    // ------------------------------------------------------------------
    //  4. View My Orders
    // ------------------------------------------------------------------
    static void viewMyOrders() {
        System.out.println("\n  ===== My Orders =====");
        System.out.print("  Your User ID: ");
        String uid = nextLine();

        if (!users.containsKey(uid)) {
            System.out.println("  [!] User ID not found.");
            return;
        }

        boolean any = false;
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            if (o.getUserId().equals(uid)) {
                System.out.println();
                System.out.println(o);   // polymorphic toString()
                any = true;
            }
        }

        if (!any) {
            System.out.println("  No orders yet for " + uid + ". Place one using option 3.");
        }
    }

    // ------------------------------------------------------------------
    //  5. Track Order
    // ------------------------------------------------------------------
    static void trackOrder() {
        System.out.println("\n  ===== Track Order =====");
        System.out.print("  Order ID (e.g. ORD0001): ");
        String oid = nextLine();

        Order found = null;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equalsIgnoreCase(oid)) {
                found = orders.get(i);
                break;
            }
        }

        if (found == null) {
            System.out.println("  [!] Order not found. Make sure you typed it correctly.");
            return;
        }

        System.out.println();
        System.out.println("  Order ID   : " + found.getOrderId());
        System.out.println("  Restaurant : " + found.getRestaurantName());
        System.out.printf ("  Bill       : Rs.%.2f%n", found.calculateBill());
        System.out.println("  Status     : " + found.getStatus());

        System.out.println("\n  Update status?");
        System.out.println("  1. OUT FOR DELIVERY");
        System.out.println("  2. DELIVERED");
        System.out.println("  3. CANCELLED");
        System.out.println("  0. Go back");
        System.out.print("  Choice: ");
        int s = nextInt("choice");

        switch (s) {
            case 1: found.updateStatus("OUT FOR DELIVERY"); break;
            case 2: found.updateStatus("DELIVERED");        break;
            case 3: found.updateStatus("CANCELLED");        break;
            case 0: break;
            default: System.out.println("  [!] Invalid.");
        }
    }

    // ------------------------------------------------------------------
    //  Polymorphism Demo
    // ------------------------------------------------------------------
    static void polymorphismDemo() {
        System.out.println("================================================================");
        System.out.println("  POLYMORPHISM DEMO  -  same call, different result by type");
        System.out.println("  Order reference type is 'Order' but actual object differs");
        System.out.println("================================================================");
        Order r = new RegularOrder("D01", 150.0, "U0", "Demo");
        Order p = new PremiumOrder("D02", 150.0, "U0", "Demo");
        System.out.printf("  RegularOrder.calculateBill(base=150) = Rs.%.2f%n", r.calculateBill());
        System.out.println("    Formula: (150 + 40) x 1.18 = 190 x 1.18");
        System.out.printf("  PremiumOrder.calculateBill(base=150) = Rs.%.2f%n", p.calculateBill());
        System.out.println("    Formula: (150x0.8 + 40) x 1.18 = 160 x 1.18");
        System.out.printf("  With SAVE10: Regular=Rs.%.2f | Premium=Rs.%.2f%n",
            Math.round(r.calculateBill() * 0.9 * 100) / 100.0,
            Math.round(p.calculateBill() * 0.9 * 100) / 100.0);
        System.out.println("================================================================");
        System.out.println("  Test user ready: U001 (Aarav Sharma) - skip registration!");
        System.out.println("================================================================");
        System.out.println();
    }

    // ------------------------------------------------------------------
    //  Sample Data
    // ------------------------------------------------------------------
    static void loadSampleData() {
        Restaurant r1 = new Restaurant("R001", "Spice Garden");
        r1.addMenuItem(new MenuItem("SG01", "Butter Chicken",        280.0));
        r1.addMenuItem(new MenuItem("SG02", "Paneer Tikka",          220.0));
        r1.addMenuItem(new MenuItem("SG03", "Garlic Naan",            50.0));
        r1.addMenuItem(new MenuItem("SG04", "Dal Makhani",           180.0));
        r1.addMenuItem(new MenuItem("SG05", "Gulab Jamun (2 pc)",     80.0));
        restaurants.add(r1);

        Restaurant r2 = new Restaurant("R002", "Pizza Palace");
        r2.addMenuItem(new MenuItem("PP01", "Margherita Pizza (M)",  299.0));
        r2.addMenuItem(new MenuItem("PP02", "BBQ Chicken Pizza (M)", 349.0));
        r2.addMenuItem(new MenuItem("PP03", "Garlic Bread (4 pcs)",   99.0));
        r2.addMenuItem(new MenuItem("PP04", "Pasta Arrabbiata",      199.0));
        r2.addMenuItem(new MenuItem("PP05", "Chocolate Lava Cake",   149.0));
        restaurants.add(r2);

        users.put("U001", new User("U001", "Aarav Sharma", "9876543210", "12 MG Road Delhi"));
    }

    // ------------------------------------------------------------------
    //  Menu
    // ------------------------------------------------------------------
    static void showMenu() {
        System.out.println();
        System.out.println("  +--------------------------------+");
        System.out.println("  |     FOOD DELIVERY APP          |");
        System.out.println("  +--------------------------------+");
        System.out.println("  |  1.  Register User             |");
        System.out.println("  |  2.  View Restaurants / Menu   |");
        System.out.println("  |  3.  Place Order               |");
        System.out.println("  |  4.  View My Orders            |");
        System.out.println("  |  5.  Track Order Status        |");
        System.out.println("  |  6.  Exit                      |");
        System.out.println("  +--------------------------------+");
        System.out.print("  >> Choice: ");
    }

    // ------------------------------------------------------------------
    //  Input helpers - robust on all platforms
    // ------------------------------------------------------------------
    static String nextLine() {
        try {
            if (sc.hasNextLine()) {
                return sc.nextLine().trim();
            }
        } catch (Exception e) { }
        return "";
    }

    static int nextInt(String label) {
        while (true) {
            String line = nextLine();
            if (line.isEmpty()) {
                System.out.print("  [!] Please enter a number: ");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("  [!] '" + line + "' is not a number. Try again: ");
            }
        }
    }

    static String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(' ');
        return sb.toString();
    }
}
