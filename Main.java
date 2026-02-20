import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// ═══════════════════════════════════════════════════════════════════════════
//  Online Food Delivery App  —  DeliveryAPP
//  Single-file version: everything in Main.java
//
//  HOW TO RUN:
//    javac Main.java
//    java Main
//
//  Or just open in VS Code and click ▶ Run
// ═══════════════════════════════════════════════════════════════════════════


// ─────────────────────────────────────────────────────────────────────────────
//  ABSTRACT BASE CLASS: Order
//  Demonstrates: abstract class, encapsulation, static field
// ─────────────────────────────────────────────────────────────────────────────
abstract class Order {

    // Private fields (Encapsulation)
    private String orderId;
    private double baseAmount;
    private String status;
    private String userId;
    private String restaurantName;

    // Static field — shared across ALL orders
    public static double deliveryCharge = 40.0;

    // Constructor
    public Order(String orderId, double baseAmount, String userId, String restaurantName) {
        this.orderId        = orderId;
        this.baseAmount     = baseAmount;
        this.userId         = userId;
        this.restaurantName = restaurantName;
        this.status         = "PLACED";
    }

    // Abstract method — each subclass MUST override this (Polymorphism)
    public abstract double calculateBill();

    // Update order delivery status
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("  ✅ Order " + orderId + " status → " + newStatus);
    }

    // Getters
    public String getOrderId()        { return orderId; }
    public double getBaseAmount()     { return baseAmount; }
    public String getStatus()         { return status; }
    public String getUserId()         { return userId; }
    public String getRestaurantName() { return restaurantName; }

    @Override
    public String toString() {
        return String.format(
            "OrderID: %-8s | Restaurant: %-20s | Base: ₹%-8.2f | Bill: ₹%-8.2f | Status: %s",
            orderId, restaurantName, baseAmount, calculateBill(), status
        );
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  RegularOrder — extends Order
//  Bill = (baseAmount + deliveryCharge) + 18% GST
//  Example: base=150 → (150+40)×1.18 = ₹224.20
// ─────────────────────────────────────────────────────────────────────────────
class RegularOrder extends Order {

    private static final double TAX_RATE = 0.18; // 18% GST

    public RegularOrder(String orderId, double baseAmount, String userId, String restaurantName) {
        super(orderId, baseAmount, userId, restaurantName);
    }

    // Polymorphism: overrides abstract calculateBill() from Order
    @Override
    public double calculateBill() {
        double subtotal = getBaseAmount() + Order.deliveryCharge;
        double tax      = subtotal * TAX_RATE;
        return Math.round((subtotal + tax) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        double subtotal = getBaseAmount() + Order.deliveryCharge;
        double tax      = subtotal * TAX_RATE;
        return super.toString() + "\n" +
               String.format("  [REGULAR]  Base ₹%.2f + Delivery ₹%.2f + GST(18%%) ₹%.2f = ₹%.2f",
                   getBaseAmount(), Order.deliveryCharge, tax, calculateBill());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  PremiumOrder — extends Order
//  Bill = (baseAmount × 0.8 + deliveryCharge) + 18% GST  [20% off base price]
//  Example: base=150 → (120+40)×1.18 = ₹188.80
// ─────────────────────────────────────────────────────────────────────────────
class PremiumOrder extends Order {

    private static final double DISCOUNT = 0.20; // 20% discount on base
    private static final double TAX_RATE = 0.18; // 18% GST

    public PremiumOrder(String orderId, double baseAmount, String userId, String restaurantName) {
        super(orderId, baseAmount, userId, restaurantName);
    }

    // Polymorphism: overrides abstract calculateBill() from Order
    @Override
    public double calculateBill() {
        double discounted = getBaseAmount() * (1 - DISCOUNT);
        double subtotal   = discounted + Order.deliveryCharge;
        double tax        = subtotal * TAX_RATE;
        return Math.round((subtotal + tax) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        double discounted = getBaseAmount() * (1 - DISCOUNT);
        double subtotal   = discounted + Order.deliveryCharge;
        double tax        = subtotal * TAX_RATE;
        return super.toString() + "\n" +
               String.format("  [PREMIUM]  Base ₹%.2f - 20%% = ₹%.2f + Delivery ₹%.2f + GST(18%%) ₹%.2f = ₹%.2f",
                   getBaseAmount(), discounted, Order.deliveryCharge, tax, calculateBill());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  User — customer entity
// ─────────────────────────────────────────────────────────────────────────────
class User {

    private String userId;
    private String name;
    private String phone;
    private String address;

    public User(String userId, String name, String phone, String address) {
        this.userId  = userId;
        this.name    = name;
        this.phone   = phone;
        this.address = address;
    }

    public String getUserId()  { return userId; }
    public String getName()    { return name; }
    public String getPhone()   { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("User[%s] %s | Phone: %s | Address: %s",
            userId, name, phone, address);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  MenuItem — single item on a restaurant's menu
// ─────────────────────────────────────────────────────────────────────────────
class MenuItem {

    private String itemId;
    private String name;
    private double price;

    public MenuItem(String itemId, String name, double price) {
        this.itemId = itemId;
        this.name   = name;
        this.price  = price;
    }

    public String getItemId() { return itemId; }
    public String getName()   { return name; }
    public double getPrice()  { return price; }

    @Override
    public String toString() {
        return String.format("  [%-4s] %-28s  ₹%.2f", itemId, name, price);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  Restaurant — has a name, ID, and ArrayList of MenuItems
// ─────────────────────────────────────────────────────────────────────────────
class Restaurant {

    private String restId;
    private String name;
    private ArrayList<MenuItem> menu;

    public Restaurant(String restId, String name) {
        this.restId = restId;
        this.name   = name;
        this.menu   = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) { menu.add(item); }

    public void displayMenu() {
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.printf( "  ║  🍽  %-36s║%n", name + " — Menu");
        System.out.println("  ╠══════════════════════════════════════════╣");
        for (MenuItem item : menu) System.out.println(item);
        System.out.println("  ╚══════════════════════════════════════════╝");
    }

    public MenuItem findItem(String itemId) {
        for (MenuItem item : menu)
            if (item.getItemId().equalsIgnoreCase(itemId)) return item;
        return null;
    }

    public String getRestId()            { return restId; }
    public String getName()              { return name; }
    public ArrayList<MenuItem> getMenu() { return menu; }

    @Override
    public String toString() {
        return String.format("  [%s] %-24s (%d items)", restId, name, menu.size());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  Main — entry point, storage, console menu
// ─────────────────────────────────────────────────────────────────────────────
public class Main {

    // ── In-memory Storage (no DB, no files) ─────────────────────────
    static ArrayList<Order>      orders      = new ArrayList<>();
    static HashMap<String, User> users       = new HashMap<>();
    static ArrayList<Restaurant> restaurants = new ArrayList<>();

    // ── Auto-ID counters ─────────────────────────────────────────────
    static int userCounter  = 2; // U001 pre-loaded, next = U002
    static int orderCounter = 1;

    // ── Coupon ───────────────────────────────────────────────────────
    static final String COUPON      = "SAVE10";
    static final double COUPON_OFF  = 0.10;

    // ── Shared Scanner ───────────────────────────────────────────────
    static Scanner sc = new Scanner(System.in);

    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        loadSampleData();
        showPolymorphismDemo();

        boolean on = true;
        while (on) {
            printMenu();
            int ch = readInt("👉 Enter choice: ");
            switch (ch) {
                case 1: registerUser();     break;
                case 2: viewRestaurants();  break;
                case 3: placeOrder();       break;
                case 4: viewMyOrders();     break;
                case 5: trackStatus();      break;
                case 6: on = false; System.out.println("\n  🍕 Thanks for using DeliveryAPP! Goodbye!\n"); break;
                default: System.out.println("  ❌ Invalid choice.");
            }
        }
        sc.close();
    }

    // ════════════════════════════════════════════════════════════════
    //  FEATURE 1 — Register User
    // ════════════════════════════════════════════════════════════════
    static void registerUser() {
        System.out.println("\n  ──── Register New User ────");
        System.out.print("  Name    : "); String name    = sc.nextLine().trim();
        System.out.print("  Phone   : "); String phone   = sc.nextLine().trim();
        System.out.print("  Address : "); String address = sc.nextLine().trim();

        String uid = "U" + String.format("%03d", userCounter++);
        users.put(uid, new User(uid, name, phone, address));
        System.out.println("\n  ✅ Registered! Your User ID: " + uid + "  (use this to place orders)");
    }

    // ════════════════════════════════════════════════════════════════
    //  FEATURE 2 — View Restaurants + Menu
    // ════════════════════════════════════════════════════════════════
    static void viewRestaurants() {
        System.out.println("\n  ──── Restaurants ────");
        for (int i = 0; i < restaurants.size(); i++)
            System.out.println("  " + (i+1) + ". " + restaurants.get(i));
        int pick = readInt("  View menu of which? (number, 0 to skip): ");
        if (pick >= 1 && pick <= restaurants.size())
            restaurants.get(pick - 1).displayMenu();
    }

    // ════════════════════════════════════════════════════════════════
    //  FEATURE 3 — Place Order
    // ════════════════════════════════════════════════════════════════
    static void placeOrder() {
        System.out.println("\n  ──── Place Order ────");
        System.out.print("  Your User ID: "); String uid = sc.nextLine().trim();
        if (!users.containsKey(uid)) { System.out.println("  ❌ User not found. Register first."); return; }

        // Pick restaurant
        System.out.println("\n  Available Restaurants:");
        for (int i = 0; i < restaurants.size(); i++)
            System.out.println("  " + (i+1) + ". " + restaurants.get(i));
        int rPick = readInt("  Select restaurant: ");
        if (rPick < 1 || rPick > restaurants.size()) { System.out.println("  ❌ Invalid."); return; }
        Restaurant rest = restaurants.get(rPick - 1);
        rest.displayMenu();

        // Add items
        double total = 0;
        System.out.println("\n  Enter item IDs one by one. Type 'done' when finished.");
        while (true) {
            System.out.print("  Item ID: ");
            String id = sc.nextLine().trim();
            if (id.equalsIgnoreCase("done")) break;
            MenuItem item = rest.findItem(id);
            if (item == null) {
                System.out.println("  ⚠  Item not found, try again.");
            } else {
                total += item.getPrice();
                System.out.printf("  ✅ Added: %-28s ₹%.2f  |  Running total: ₹%.2f%n",
                    item.getName(), item.getPrice(), total);
            }
        }
        if (total == 0) { System.out.println("  ❌ No items added."); return; }

        // Order type
        System.out.println("\n  Order Type:");
        System.out.println("  1. Regular  — full price + delivery + 18% GST");
        System.out.println("  2. Premium  — 20% OFF base + delivery + 18% GST");
        int type = readInt("  Choose (1/2): ");

        // Coupon
        System.out.print("  Coupon code (Enter to skip): ");
        String cpn = sc.nextLine().trim();
        boolean hasCoupon = cpn.equalsIgnoreCase(COUPON);
        if (!cpn.isEmpty() && !hasCoupon) System.out.println("  ⚠  Invalid coupon.");

        // Create Order — stored as abstract type Order (Polymorphism!)
        String oid = "ORD" + String.format("%04d", orderCounter++);
        Order order = (type == 2)
            ? new PremiumOrder(oid, total, uid, rest.getName())
            : new RegularOrder(oid, total, uid, rest.getName());

        orders.add(order);

        // calculateBill() called on Order reference → runtime polymorphism
        double bill = order.calculateBill();
        double finalBill = hasCoupon
            ? Math.round(bill * (1 - COUPON_OFF) * 100.0) / 100.0
            : bill;

        // Assign delivery rider (simulation)
        String[] riders = {"Raju (⭐4.8)", "Priya (⭐4.9)", "Arjun (⭐4.7)", "Meera (⭐4.6)"};
        String rider = riders[(int)(Math.random() * riders.length)];
        order.updateStatus("ASSIGNED → " + rider);

        // Print receipt
        System.out.println("\n  ╔══════════════════════════════════╗");
        System.out.println("  ║         🧾 ORDER RECEIPT          ║");
        System.out.println("  ╠══════════════════════════════════╣");
        System.out.printf( "  ║  Order ID   : %-18s║%n", oid);
        System.out.printf( "  ║  Restaurant : %-18s║%n", rest.getName());
        System.out.printf( "  ║  Type       : %-18s║%n", type == 2 ? "PREMIUM" : "REGULAR");
        System.out.printf( "  ║  Base Amt   : ₹%-17.2f║%n", total);
        System.out.printf( "  ║  Bill+Tax   : ₹%-17.2f║%n", bill);
        if (hasCoupon)
        System.out.printf( "  ║  SAVE10(-10%%): -₹%-16.2f║%n", bill - finalBill);
        System.out.println("  ╠══════════════════════════════════╣");
        System.out.printf( "  ║  FINAL BILL : ₹%-17.2f║%n", finalBill);
        System.out.println("  ╚══════════════════════════════════╝");
    }

    // ════════════════════════════════════════════════════════════════
    //  FEATURE 4 — View My Orders
    // ════════════════════════════════════════════════════════════════
    static void viewMyOrders() {
        System.out.println("\n  ──── My Orders ────");
        System.out.print("  Your User ID: "); String uid = sc.nextLine().trim();
        boolean found = false;
        for (Order o : orders) {
            if (o.getUserId().equals(uid)) {
                System.out.println("\n  " + o); // calls overridden toString → polymorphism
                found = true;
            }
        }
        if (!found) System.out.println("  No orders found for " + uid + ".");
    }

    // ════════════════════════════════════════════════════════════════
    //  FEATURE 5 — Track & Update Order Status
    // ════════════════════════════════════════════════════════════════
    static void trackStatus() {
        System.out.println("\n  ──── Track Order ────");
        System.out.print("  Order ID: "); String oid = sc.nextLine().trim();
        Order o = findOrder(oid);
        if (o == null) { System.out.println("  ❌ Order not found."); return; }

        System.out.printf("  Order : %s%n  Status: %s%n  Bill  : ₹%.2f%n",
            o.getOrderId(), o.getStatus(), o.calculateBill());

        System.out.print("\n  Update status? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  1. OUT_FOR_DELIVERY");
            System.out.println("  2. DELIVERED");
            System.out.println("  3. CANCELLED");
            int s = readInt("  Choice: ");
            switch(s) {
                case 1: o.updateStatus("OUT_FOR_DELIVERY"); break;
                case 2: o.updateStatus("DELIVERED");        break;
                case 3: o.updateStatus("CANCELLED");        break;
                default: System.out.println("  Invalid.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  POLYMORPHISM DEMO — runs once at startup
    // ════════════════════════════════════════════════════════════════
    static void showPolymorphismDemo() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        ✨  POLYMORPHISM DEMO  (runs at startup)               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║  Same call: order.calculateBill() — different result by type  ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");

        // Both declared as type Order — dynamic dispatch decides which calculateBill() runs
        Order regular = new RegularOrder("DEMO01", 150.0, "U001", "Demo");
        Order premium = new PremiumOrder("DEMO02", 150.0, "U001", "Demo");

        System.out.printf("║  RegularOrder.calculateBill(base=150) → ₹%-6.2f               ║%n", regular.calculateBill());
        System.out.println("║    Formula: (150 + 40) × 1.18 = 190 × 1.18                   ║");
        System.out.printf("║  PremiumOrder.calculateBill(base=150) → ₹%-6.2f               ║%n", premium.calculateBill());
        System.out.println("║    Formula: (150×0.8 + 40) × 1.18 = 160 × 1.18               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.printf( "║  With SAVE10 coupon → Regular: ₹%-6.2f | Premium: ₹%-6.2f   ║%n",
            Math.round(regular.calculateBill() * 0.9 * 100) / 100.0,
            Math.round(premium.calculateBill() * 0.9 * 100) / 100.0);
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    // ════════════════════════════════════════════════════════════════
    //  SAMPLE DATA — 2 restaurants, 5 items each, 1 pre-loaded user
    // ════════════════════════════════════════════════════════════════
    static void loadSampleData() {
        // Spice Garden
        Restaurant r1 = new Restaurant("R001", "Spice Garden");
        r1.addMenuItem(new MenuItem("SG01", "Butter Chicken",       280.0));
        r1.addMenuItem(new MenuItem("SG02", "Paneer Tikka",         220.0));
        r1.addMenuItem(new MenuItem("SG03", "Garlic Naan",           50.0));
        r1.addMenuItem(new MenuItem("SG04", "Dal Makhani",          180.0));
        r1.addMenuItem(new MenuItem("SG05", "Gulab Jamun (2 pc)",    80.0));
        restaurants.add(r1);

        // Pizza Palace
        Restaurant r2 = new Restaurant("R002", "Pizza Palace");
        r2.addMenuItem(new MenuItem("PP01", "Margherita Pizza (M)", 299.0));
        r2.addMenuItem(new MenuItem("PP02", "BBQ Chicken Pizza (M)",349.0));
        r2.addMenuItem(new MenuItem("PP03", "Garlic Bread (4 pcs)",  99.0));
        r2.addMenuItem(new MenuItem("PP04", "Pasta Arrabbiata",     199.0));
        r2.addMenuItem(new MenuItem("PP05", "Chocolate Lava Cake",  149.0));
        restaurants.add(r2);

        // Pre-loaded user for quick testing
        users.put("U001", new User("U001", "Aarav Sharma", "9876543210", "12 MG Road, Delhi"));
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════
    static Order findOrder(String oid) {
        for (Order o : orders)
            if (o.getOrderId().equalsIgnoreCase(oid)) return o;
        return null;
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  ⚠  Enter a valid number."); }
        }
    }

    static void printMenu() {
        System.out.println("\n  ╔════════════════════════════════╗");
        System.out.println("  ║   🍕  DeliveryAPP  🛵           ║");
        System.out.println("  ╠════════════════════════════════╣");
        System.out.println("  ║  1.  Register User             ║");
        System.out.println("  ║  2.  View Restaurants / Menu   ║");
        System.out.println("  ║  3.  Place Order               ║");
        System.out.println("  ║  4.  View My Orders            ║");
        System.out.println("  ║  5.  Track Order Status        ║");
        System.out.println("  ║  6.  Exit                      ║");
        System.out.println("  ╚════════════════════════════════╝");
    }
}
