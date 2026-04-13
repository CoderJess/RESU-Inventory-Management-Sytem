import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;

// ─────────────────────────────────────────────
//  Design System Constants
// ─────────────────────────────────────────────
class DS {
    // Palette — deep slate + amber accent
    static final Color BG_DARK   = new Color(0x0F1117);
    static final Color BG_PANEL  = new Color(0x1A1D27);
    static final Color BG_CARD   = new Color(0x22263A);
    static final Color ACCENT    = new Color(0xF5A623);
    static final Color ACCENT2   = new Color(0xE8913A);
    static final Color TEXT_PRI  = new Color(0xECEFF4);
    static final Color TEXT_SEC  = new Color(0x8890A4);
    static final Color BORDER    = new Color(0x2E3347);
    static final Color SUCCESS   = new Color(0x4CAF82);
    static final Color DANGER    = new Color(0xE05C5C);

    // Typography
    static final Font  FONT_TITLE  = new Font("Georgia", Font.BOLD, 22);
    static final Font  FONT_HEAD   = new Font("Georgia", Font.BOLD, 15);
    static final Font  FONT_BODY   = new Font("SansSerif", Font.PLAIN, 13);
    static final Font  FONT_LABEL  = new Font("SansSerif", Font.BOLD,  12);
    static final Font  FONT_BTN    = new Font("SansSerif", Font.BOLD,  13);
    static final Font  FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);

    static void applyGlobalDefaults() {
        UIManager.put("Panel.background",       BG_PANEL);
        UIManager.put("OptionPane.background",  BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRI);
        UIManager.put("TextField.background",   BG_CARD);
        UIManager.put("TextField.foreground",   TEXT_PRI);
        UIManager.put("TextField.caretForeground", ACCENT);
        UIManager.put("TextField.border",       BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        UIManager.put("PasswordField.background",  BG_CARD);
        UIManager.put("PasswordField.foreground",  TEXT_PRI);
        UIManager.put("PasswordField.caretForeground", ACCENT);
        UIManager.put("ScrollPane.background",  BG_PANEL);
        UIManager.put("ScrollBarUI", "javax.swing.plaf.basic.BasicScrollBarUI");
        UIManager.put("Button.background",      ACCENT);
        UIManager.put("Button.foreground",      BG_DARK);
        UIManager.put("Label.foreground",       TEXT_PRI);
        UIManager.put("Table.background",       BG_CARD);
        UIManager.put("Table.foreground",       TEXT_PRI);
        UIManager.put("Table.gridColor",        BORDER);
        UIManager.put("Table.selectionBackground", ACCENT);
        UIManager.put("Table.selectionForeground", BG_DARK);
        UIManager.put("TableHeader.background", BG_PANEL);
        UIManager.put("TableHeader.foreground", TEXT_SEC);
        UIManager.put("ComboBox.background",    BG_CARD);
        UIManager.put("ComboBox.foreground",    TEXT_PRI);
        UIManager.put("TextArea.background",    BG_CARD);
        UIManager.put("TextArea.foreground",    TEXT_PRI);
        UIManager.put("TextArea.caretForeground", ACCENT);
    }

    /** Primary button */
    static JButton btn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT2.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT2);
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BG_DARK);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(160, 36));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Secondary / ghost button */
    static JButton btnGhost(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = getModel().isRollover() ? ACCENT : BORDER;
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(getModel().isRollover() ? ACCENT : TEXT_SEC);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(160, 36));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Text field */
    static JTextField field(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(TEXT_SEC);
                    g2.setFont(FONT_BODY);
                    g2.drawString(placeholder, 10, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 1);
                }
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setForeground(TEXT_PRI);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, 6),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setPreferredSize(new Dimension(200, 36));
        return tf;
    }

    /** Password field */
    static JPasswordField pwdField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setOpaque(true);
        pf.setBackground(BG_CARD);
        pf.setForeground(TEXT_PRI);
        pf.setCaretColor(ACCENT);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, 6),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        pf.setPreferredSize(new Dimension(200, 36));
        return pf;
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_PRI);
        l.setFont(FONT_LABEL);
        return l;
    }

    static JLabel labelSec(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SEC);
        l.setFont(FONT_SMALL);
        return l;
    }

    /** Dark-styled JTable */
    static JTable styledTable(Object[] cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : BG_PANEL);
                    c.setForeground(TEXT_PRI);
                } else {
                    c.setBackground(ACCENT);
                    c.setForeground(BG_DARK);
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return c;
            }
        };
        t.setFont(FONT_BODY);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setFont(FONT_LABEL);
        t.getTableHeader().setBackground(BG_PANEL);
        t.getTableHeader().setForeground(TEXT_SEC);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        t.setSelectionBackground(ACCENT);
        t.setSelectionForeground(BG_DARK);
        return t;
    }

    /** Styled scroll pane */
    static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(BG_PANEL);
        sp.getViewport().setBackground(BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setBackground(BG_PANEL);
        sp.getHorizontalScrollBar().setBackground(BG_PANEL);
        return sp;
    }

    /** Card panel */
    static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER, 10),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return p;
    }

    /** Dark frame base */
    static JFrame frame(String title, int w, int h) {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(w, h);
        f.setLocationRelativeTo(null);
        f.getContentPane().setBackground(BG_DARK);
        return f;
    }

    /** Show styled message dialog */
    static void msg(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, styledMsg(text), "Notice",
            JOptionPane.PLAIN_MESSAGE);
    }

    private static JPanel styledMsg(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        JLabel l = new JLabel("<html><body style='width:260px;color:#ECF0F4;font-family:SansSerif'>" + text + "</body></html>");
        l.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        p.add(l);
        return p;
    }
}

// ─────────────────────────────────────────────
//  Rounded border helper
// ─────────────────────────────────────────────
class RoundedBorder extends AbstractBorder {
    private final Color color;
    private final int radius;
    RoundedBorder(Color c, int r) { color = c; radius = r; }
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
        g2.dispose();
    }
    @Override public Insets getBorderInsets(Component c)          { return new Insets(1,1,1,1); }
    @Override public Insets getBorderInsets(Component c, Insets i){ i.set(1,1,1,1); return i; }
}

// ─────────────────────────────────────────────
//  Accent separator
// ─────────────────────────────────────────────
class AccentSeparator extends JPanel {
    AccentSeparator() {
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 2));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        setBackground(DS.ACCENT);
        setBorder(null);
        setOpaque(true);
    }
}

// ─────────────────────────────────────────────────────────
//  Main Application Class
// ─────────────────────────────────────────────────────────
public class InventoryManagementSystemGUI {

    private JFrame   stockFrame;
    private JTable   stockTable;
    private Customer customer;
    private JFrame   loginFrame;
    private StockInventory     stockInventory;
    private CustomerDatabase   customerDatabase;
    private SupplierDatabase   supplierDatabase;

    public InventoryManagementSystemGUI() {
        DS.applyGlobalDefaults();
        stockInventory    = new StockInventory();
        customerDatabase  = new CustomerDatabase();
        supplierDatabase  = new SupplierDatabase();
        showSplashScreen();
    }

    // ── Splash ──────────────────────────────────────────
    private void showSplashScreen() {
        JWindow splash = new JWindow();
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(DS.BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle grid
                g2.setColor(new Color(0x1A1D27));
                for (int i = 0; i < getWidth(); i += 32)
                    g2.drawLine(i, 0, i, getHeight());
                for (int j = 0; j < getHeight(); j += 32)
                    g2.drawLine(0, j, getWidth(), j);
            }
        };
        root.setPreferredSize(new Dimension(480, 280));
        root.setBackground(DS.BG_DARK);

        // logo / title area
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        // Amber accent bar
        JPanel bar = new JPanel();
        bar.setBackground(DS.ACCENT);
        bar.setPreferredSize(new Dimension(56, 4));
        bar.setMaximumSize(new Dimension(56, 4));
        center.add(bar);
        center.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel title = new JLabel("RESU IMS");
        title.setFont(new Font("Georgia", Font.BOLD, 36));
        title.setForeground(DS.TEXT_PRI);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(title);

        JLabel sub = new JLabel("Inventory Management System");
        sub.setFont(DS.FONT_BODY);
        sub.setForeground(DS.TEXT_SEC);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(sub);

        center.add(Box.createRigidArea(new Dimension(0, 32)));

        // Loading bar
        JProgressBar prog = new JProgressBar(0, 100);
        prog.setStringPainted(false);
        prog.setMaximumSize(new Dimension(400, 4));
        prog.setPreferredSize(new Dimension(400, 4));
        prog.setBackground(DS.BORDER);
        prog.setForeground(DS.ACCENT);
        prog.setBorder(null);
        prog.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(prog);

        root.add(center, BorderLayout.CENTER);

        // Bottom strip
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 40, 16, 40));
        JLabel ver = new JLabel("v1.0  •  © RESU Ltd.");
        ver.setFont(DS.FONT_SMALL);
        ver.setForeground(DS.TEXT_SEC);
        bottom.add(ver, BorderLayout.WEST);
        root.add(bottom, BorderLayout.SOUTH);

        splash.getContentPane().add(root);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        // Animate progress bar then show login
        Timer anim = new Timer(25, null);
        int[] val = {0};
        anim.addActionListener(ae -> {
            val[0] += 3;
            prog.setValue(Math.min(val[0], 100));
            if (val[0] >= 100) {
                anim.stop();
                Timer delay = new Timer(300, ev -> {
                    splash.dispose();
                    showLoginScreen();
                });
                delay.setRepeats(false);
                delay.start();
            }
        });
        anim.start();
    }

    // ── Login ────────────────────────────────────────────
    private void showLoginScreen() {
        loginFrame = new JFrame("RESU IMS — Sign In");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(460, 380);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(DS.BG_DARK);
        loginFrame.setLayout(new GridBagLayout());

        // Card
        JPanel card = new JPanel();
        card.setBackground(DS.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(DS.BORDER, 12),
            BorderFactory.createEmptyBorder(32, 36, 32, 36)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("RESU IMS");
        brand.setFont(DS.FONT_TITLE);
        brand.setForeground(DS.TEXT_PRI);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(DS.FONT_BODY);
        sub.setForeground(DS.TEXT_SEC);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(DS.ACCENT);
        accentBar.setPreferredSize(new Dimension(40, 3));
        accentBar.setMaximumSize(new Dimension(40, 3));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = DS.field("Username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = DS.pwdField("Password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = DS.btn("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(brand);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(accentBar);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(sub);
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(DS.labelSec("USERNAME"));
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(DS.labelSec("PASSWORD"));
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(loginBtn);

        loginFrame.add(card);
        loginFrame.setVisible(true);

        ActionListener doLogin = e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            String name = authenticateUser(user, pass);
            if (name != null) {
                DS.msg(loginFrame, "Welcome back, <b>" + name + "</b>.");
                loginFrame.dispose();
                showMainMenu();
            } else {
                DS.msg(loginFrame, "Invalid credentials. Please try again.");
                passwordField.setText("");
            }
        };
        loginBtn.addActionListener(doLogin);
        passwordField.addActionListener(doLogin);
    }

    private String authenticateUser(String username, String password) {
        try {
            File file = new File("ResuStaffUsers.txt");
            if (!file.exists()) return null;
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line;
            while ((line = r.readLine()) != null) {
                String[] c = line.split(",");
                if (c.length < 4) continue;
                if (c[0].trim().equals(username) && c[3].trim().equals(password)) {
                    r.close();
                    return c[1].trim();
                }
            }
            r.close();
        } catch (IOException ex) {
            DS.msg(null, "Error reading user data: " + ex.getMessage());
        }
        return null;
    }

    // ── Main Menu ────────────────────────────────────────
    private void showMainMenu() {
        JFrame menu = new JFrame("RESU IMS — Dashboard");
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(580, 420);
        menu.setLocationRelativeTo(null);
        menu.getContentPane().setBackground(DS.BG_DARK);
        menu.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DS.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, DS.BORDER),
            BorderFactory.createEmptyBorder(18, 28, 18, 28)));
        JLabel title = new JLabel("Dashboard");
        title.setFont(DS.FONT_TITLE);
        title.setForeground(DS.TEXT_PRI);
        JLabel sub = new JLabel("RESU Inventory Management System");
        sub.setFont(DS.FONT_SMALL);
        sub.setForeground(DS.TEXT_SEC);
        JPanel hText = new JPanel();
        hText.setOpaque(false);
        hText.setLayout(new BoxLayout(hText, BoxLayout.Y_AXIS));
        hText.add(title);
        hText.add(sub);
        header.add(hText, BorderLayout.WEST);

        // Accent dot
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DS.ACCENT);
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));
        header.add(dot, BorderLayout.EAST);

        // Grid of nav cards
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(DS.BG_DARK);
        grid.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        String[][] navItems = {
            {"Stock Inventory",    "Manage items, quantities & pricing",  "📦"},
            {"Customer Database",  "Customers, orders & payments",        "👤"},
            {"Supplier Database",  "Supplier contacts & expenditures",    "🏭"},
            {"Analytics",          "Usage reports & dashboards",          "📊"}
        };
        Runnable[] actions = {
            this::showStockInventoryWindow,
            this::showCustomerDatabaseWindow,
            this::showSupplierDatabaseWindow,
            this::showAnalyticsWindow
        };

        for (int i = 0; i < navItems.length; i++) {
            final int idx = i;
            JPanel card = new JPanel() {
                boolean hover = false;
                { addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { actions[idx].run(); }
                }); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(hover ? new Color(0x2A2F45) : DS.BG_CARD);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    if (hover) {
                        g2.setColor(DS.ACCENT);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setOpaque(false);
            card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(navItems[i][2]);
            icon.setFont(new Font("SansSerif", Font.PLAIN, 28));
            icon.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel name = new JLabel(navItems[i][0]);
            name.setFont(DS.FONT_HEAD);
            name.setForeground(DS.TEXT_PRI);
            name.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel desc = new JLabel(navItems[i][1]);
            desc.setFont(DS.FONT_SMALL);
            desc.setForeground(DS.TEXT_SEC);
            desc.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(icon);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(name);
            card.add(Box.createRigidArea(new Dimension(0, 4)));
            card.add(desc);
            grid.add(card);
        }

        menu.add(header, BorderLayout.NORTH);
        menu.add(grid,   BorderLayout.CENTER);
        menu.setVisible(true);
    }

    // ── Stock Inventory Window ───────────────────────────
    private void showStockInventoryWindow() {
        stockFrame = DS.frame("RESU IMS — Stock Inventory", 900, 640);
        stockFrame.setLayout(new BorderLayout(0, 0));

        stockTable = DS.styledTable(new Object[]{"Item Name", "Quantity", "Price"});
        populateTableFromFile(stockTable);

        JPanel sidebar = buildStockSidebar();

        stockFrame.add(sidebar,                       BorderLayout.WEST);
        stockFrame.add(DS.scroll(stockTable),         BorderLayout.CENTER);
        stockFrame.setVisible(true);
    }

    private JPanel buildStockSidebar() {
        JPanel side = new JPanel();
        side.setBackground(DS.BG_PANEL);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, DS.BORDER),
            BorderFactory.createEmptyBorder(24, 20, 24, 20)));
        side.setPreferredSize(new Dimension(240, 0));

        JLabel title = new JLabel("Stock Inventory");
        title.setFont(DS.FONT_HEAD);
        title.setForeground(DS.TEXT_PRI);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(title);
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(new AccentSeparator());
        side.add(Box.createRigidArea(new Dimension(0, 20)));

        // Input fields
        JTextField itemNameField  = DS.field("Item name");
        JTextField quantityField  = DS.field("Quantity");
        JTextField priceField     = DS.field("Price");
        for (JTextField f : new JTextField[]{itemNameField, quantityField, priceField}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        side.add(DS.labelSec("ITEM NAME"));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(itemNameField);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(DS.labelSec("QUANTITY"));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(quantityField);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(DS.labelSec("UNIT PRICE"));
        side.add(Box.createRigidArea(new Dimension(0, 4)));
        side.add(priceField);
        side.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JButton addBtn      = DS.btn("Add Stock");
        JButton updateBtn   = DS.btn("Update Stock");
        JButton calcBtn     = DS.btnGhost("Calculate Value");
        JButton searchBtn   = DS.btnGhost("Search Items");
        JButton ooStockBtn  = DS.btnGhost("Out of Stock");
        JButton returnBtn   = DS.btnGhost("← Return");

        for (JButton b : new JButton[]{addBtn, updateBtn, calcBtn, searchBtn, ooStockBtn, returnBtn}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        side.add(addBtn);
        side.add(Box.createRigidArea(new Dimension(0, 8)));
        side.add(updateBtn);
        side.add(Box.createRigidArea(new Dimension(0, 16)));
        side.add(calcBtn);
        side.add(Box.createRigidArea(new Dimension(0, 8)));
        side.add(searchBtn);
        side.add(Box.createRigidArea(new Dimension(0, 8)));
        side.add(ooStockBtn);
        side.add(Box.createVerticalGlue());
        side.add(returnBtn);

        // Wire up actions
        addBtn.addActionListener(e -> {
            String name = itemNameField.getText();
            try {
                int qty   = Integer.parseInt(quantityField.getText());
                double pr = Double.parseDouble(priceField.getText());
                stockInventory.addStock(name, "", qty, pr);
                ((DefaultTableModel) stockTable.getModel()).addRow(new Object[]{name, qty, pr});
                try (FileWriter w = new FileWriter("stock_inventory.txt", true)) {
                    w.write(name + "," + qty + "," + pr + "\n");
                } catch (IOException io) {
                    DS.msg(side, "File error: " + io.getMessage());
                }
                DS.msg(side, "Stock added successfully.");
                itemNameField.setText(""); quantityField.setText(""); priceField.setText("");
            } catch (NumberFormatException ex) {
                DS.msg(side, "Invalid quantity or price.");
            }
        });

        updateBtn.addActionListener(e -> {
            String[] opts = {"Add New Item", "Delete Existing Item"};
            int ch = JOptionPane.showOptionDialog(stockFrame, "Choose action:", "Update Stock",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
            if (ch == 0) addNewStock();
            else if (ch == 1) deleteExistingStock();
        });

        calcBtn.addActionListener(e ->
            DS.msg(side, "Total Inventory Value: $" + String.format("%.2f", stockInventory.calculateInventoryValue())));

        searchBtn.addActionListener(e -> showSearchWindow());

        ooStockBtn.addActionListener(e -> displayOutOfStockItems());

        returnBtn.addActionListener(e -> stockFrame.setVisible(false));

        return side;
    }

    private void addNewStock() {
        JTextField n = DS.field("Item name"), q = DS.field("Quantity"), p = DS.field("Price");
        Object[] msg = {"Item Name:", n, "Quantity:", q, "Price:", p};
        if (JOptionPane.showConfirmDialog(stockFrame, msg, "Add New Stock", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String name = n.getText();
                int qty = Integer.parseInt(q.getText());
                double price = Double.parseDouble(p.getText());
                if (itemExists(name)) { DS.msg(stockFrame, "Item already exists."); return; }
                stockInventory.addStock(name, "", qty, price);
                appendToFile(name, qty, price);
                ((DefaultTableModel) stockTable.getModel()).addRow(new Object[]{name, qty, price});
                DS.msg(stockFrame, "Stock added successfully.");
            } catch (NumberFormatException ex) {
                DS.msg(stockFrame, "Invalid quantity or price.");
            }
        }
    }

    private void deleteExistingStock() {
        String name = JOptionPane.showInputDialog(stockFrame, "Enter item name to delete:");
        if (name != null && !name.trim().isEmpty()) {
            if (removeFromFile(name)) {
                DefaultTableModel m = (DefaultTableModel) stockTable.getModel();
                for (int i = 0; i < m.getRowCount(); i++) {
                    if (((String) m.getValueAt(i, 0)).equalsIgnoreCase(name)) {
                        m.removeRow(i);
                        DS.msg(stockFrame, "Item deleted successfully.");
                        return;
                    }
                }
                DS.msg(stockFrame, "Item not found in table.");
            } else {
                DS.msg(stockFrame, "Item not found in inventory file.");
            }
        }
    }

    private boolean removeFromFile(String itemName) {
        File in = new File("stock_inventory.txt"), tmp = new File("temp_stock_inventory.txt");
        boolean found = false;
        try (BufferedReader r = new BufferedReader(new FileReader(in));
             PrintWriter w = new PrintWriter(new FileWriter(tmp))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",");
                if (d[0].equalsIgnoreCase(itemName)) { found = true; continue; }
                w.println(line);
            }
            if (in.delete()) tmp.renameTo(in);
        } catch (IOException e) { DS.msg(stockFrame, "File error: " + e.getMessage()); }
        return found;
    }

    private boolean itemExists(String name) {
        try (BufferedReader r = new BufferedReader(new FileReader("stock_inventory.txt"))) {
            String line;
            while ((line = r.readLine()) != null)
                if (line.split(",")[0].equalsIgnoreCase(name)) return true;
        } catch (IOException e) { DS.msg(stockFrame, "File error: " + e.getMessage()); }
        return false;
    }

    private void appendToFile(String name, int qty, double price) {
        try (FileWriter w = new FileWriter("stock_inventory.txt", true)) {
            w.write(name + "," + qty + "," + price + "\n");
        } catch (IOException e) { DS.msg(stockFrame, "File error: " + e.getMessage()); }
    }

    private void populateTableFromFile(JTable table) {
        try (BufferedReader r = new BufferedReader(new FileReader("stock_inventory.txt"))) {
            String line;
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 3)
                    m.addRow(new Object[]{d[0], Integer.parseInt(d[1]), Double.parseDouble(d[2])});
            }
        } catch (IOException | NumberFormatException ignored) {}
    }

    private void displayOutOfStockItems() {
        JFrame f = DS.frame("Out of Stock Items", 420, 360);
        f.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DS.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,DS.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        JLabel title = new JLabel("Out of Stock Items");
        title.setFont(DS.FONT_HEAD);
        title.setForeground(DS.TEXT_PRI);
        header.add(title);

        JTable t = DS.styledTable(new Object[]{"Item Name", "Quantity"});
        ArrayList<Stock> items = stockInventory.getOutOfStockItems();
        for (Stock s : items)
            if (s.getQuantity() == 0)
                ((DefaultTableModel) t.getModel()).addRow(new Object[]{s.getItemName(), 0});

        if (((DefaultTableModel) t.getModel()).getRowCount() == 0) {
            JLabel ok = new JLabel("✓  All items are in stock.", SwingConstants.CENTER);
            ok.setForeground(DS.SUCCESS);
            ok.setFont(DS.FONT_BODY);
            f.add(header, BorderLayout.NORTH);
            f.add(ok, BorderLayout.CENTER);
        } else {
            f.add(header, BorderLayout.NORTH);
            f.add(DS.scroll(t), BorderLayout.CENTER);
        }
        f.setVisible(true);
    }

    private void showSearchWindow() {
        JDialog dlg = new JDialog((JFrame) null, "Search Inventory", true);
        dlg.setSize(440, 300);
        dlg.setLocationRelativeTo(null);
        dlg.getContentPane().setBackground(DS.BG_DARK);
        dlg.setLayout(new BorderLayout());

        JPanel card = DS.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Search Inventory");
        title.setFont(DS.FONT_HEAD);
        title.setForeground(DS.TEXT_PRI);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField searchField = DS.field("Enter item name...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        JButton go     = DS.btn("Search");
        JButton cancel = DS.btnGhost("Cancel");
        btnRow.add(go);
        btnRow.add(Box.createRigidArea(new Dimension(10, 0)));
        btnRow.add(cancel);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(DS.labelSec("ITEM NAME"));
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(searchField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btnRow);

        dlg.add(card, BorderLayout.CENTER);

        go.addActionListener(e -> {
            displaySearchResults(searchField.getText());
            dlg.dispose();
        });
        cancel.addActionListener(e -> dlg.dispose());
        searchField.addActionListener(e -> go.doClick());

        dlg.setVisible(true);
    }

    private void displaySearchResults(String itemName) {
        JDialog dlg = new JDialog((JFrame) null, "Search Results", true);
        dlg.setSize(360, 280);
        dlg.setLocationRelativeTo(null);
        dlg.getContentPane().setBackground(DS.BG_DARK);
        dlg.setLayout(new BorderLayout());

        JPanel card = DS.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        try {
            Stock s = stockInventory.findStockByName(itemName);
            addResultRow(card, "Item Name", s.getItemName());
            addResultRow(card, "Quantity",  String.valueOf(s.getQuantity()));
            addResultRow(card, "Price",     String.format("$%.2f", s.getPrice()));
        } catch (Exception e) {
            JLabel err = new JLabel("Item \"" + itemName + "\" not found.");
            err.setForeground(DS.DANGER);
            err.setFont(DS.FONT_BODY);
            card.add(err);
        }

        JButton close = DS.btnGhost("Close");
        close.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(DS.BG_CARD);
        btnRow.add(close);

        dlg.add(card, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addResultRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,DS.BORDER),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        JLabel l = new JLabel(label);
        l.setForeground(DS.TEXT_SEC);
        l.setFont(DS.FONT_SMALL);
        JLabel v = new JLabel(value);
        v.setForeground(DS.TEXT_PRI);
        v.setFont(DS.FONT_BODY);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 2)));
    }

    // ── Customer Window ──────────────────────────────────
    private void showCustomerDatabaseWindow() {
        JFrame frame = DS.frame("RESU IMS — Customer Database", 900, 640);
        frame.setLayout(new BorderLayout());

        JTable cTable = DS.styledTable(new Object[]{"Name", "Contact Info", "Address"});

        JPanel sidebar = buildCustomerSidebar(cTable, frame);
        frame.add(sidebar,             BorderLayout.WEST);
        frame.add(DS.scroll(cTable),   BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel buildCustomerSidebar(JTable cTable, JFrame frame) {
        JPanel side = new JPanel();
        side.setBackground(DS.BG_PANEL);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,0,1,DS.BORDER),
            BorderFactory.createEmptyBorder(24, 20, 24, 20)));
        side.setPreferredSize(new Dimension(240, 0));

        JLabel title = new JLabel("Customers");
        title.setFont(DS.FONT_HEAD);
        title.setForeground(DS.TEXT_PRI);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(title);
        side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(new AccentSeparator());
        side.add(Box.createRigidArea(new Dimension(0,18)));

        JTextField nameField    = DS.field("Full name");
        JTextField contactField = DS.field("Contact info");
        JTextField addressField = DS.field("Address");
        JTextField orderField   = DS.field("Order number");

        for (JTextField f : new JTextField[]{nameField, contactField, addressField, orderField}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        side.add(DS.labelSec("NAME"));            side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(nameField);                       side.add(Box.createRigidArea(new Dimension(0,10)));
        side.add(DS.labelSec("CONTACT"));         side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(contactField);                   side.add(Box.createRigidArea(new Dimension(0,10)));
        side.add(DS.labelSec("ADDRESS"));         side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(addressField);                   side.add(Box.createRigidArea(new Dimension(0,10)));
        side.add(DS.labelSec("ORDER NUMBER"));    side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(orderField);                     side.add(Box.createRigidArea(new Dimension(0,18)));

        JButton addBtn      = DS.btn("Add Customer");
        JButton searchName  = DS.btn("Search by Name");
        JButton searchOrder = DS.btn("Search by Order");
        JButton updateBtn   = DS.btnGhost("Update Selected");
        JButton payBtn      = DS.btnGhost("Add Payment");
        JButton orderBtn    = DS.btnGhost("Add Order");
        JButton returnBtn   = DS.btnGhost("← Return");

        for (JButton b : new JButton[]{addBtn, searchName, searchOrder, updateBtn, payBtn, orderBtn, returnBtn}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        side.add(addBtn);     side.add(Box.createRigidArea(new Dimension(0,8)));
        side.add(searchName); side.add(Box.createRigidArea(new Dimension(0,8)));
        side.add(searchOrder);side.add(Box.createRigidArea(new Dimension(0,16)));
        side.add(updateBtn);  side.add(Box.createRigidArea(new Dimension(0,8)));
        side.add(payBtn);     side.add(Box.createRigidArea(new Dimension(0,8)));
        side.add(orderBtn);   side.add(Box.createVerticalGlue());
        side.add(returnBtn);

        // Wire actions
        addBtn.addActionListener(e -> {
            String n = nameField.getText(), c = contactField.getText(), a = addressField.getText();
            if (n.isEmpty() || c.isEmpty() || a.isEmpty()) { DS.msg(side, "All fields must be filled."); return; }
            customerDatabase.addCustomer(n, c, a);
            ((DefaultTableModel) cTable.getModel()).addRow(new Object[]{n, c, a});
            DS.msg(side, "Customer added successfully.");
            nameField.setText(""); contactField.setText(""); addressField.setText("");
        });

        searchName.addActionListener(e -> {
            Customer cu = customerDatabase.searchCustomerByName(nameField.getText());
            DS.msg(side, cu != null ? "Found: " + cu.getName() : "Customer not found.");
        });

        searchOrder.addActionListener(e -> {
            String ord = orderField.getText();
            if (ord.isEmpty()) { DS.msg(side, "Enter an order number."); return; }
            Customer cu = customerDatabase.searchCustomerByOrder(ord);
            DS.msg(side, cu != null ? "Found: " + cu.getName() + " — Order: " + ord : "Order not found.");
        });

        updateBtn.addActionListener(e -> {
            int row = cTable.getSelectedRow();
            if (row < 0) { DS.msg(side, "Select a customer to update."); return; }
            String nm = (String) cTable.getValueAt(row, 0);
            String ci = (String) cTable.getValueAt(row, 1);
            String newC = JOptionPane.showInputDialog("New contact info:", ci);
            String newA = JOptionPane.showInputDialog("New address:", cTable.getValueAt(row, 2));
            if (newC != null && newA != null && !newC.isEmpty() && !newA.isEmpty()) {
                customerDatabase.updateCustomer(nm, ci, newC, newA);
                cTable.setValueAt(newC, row, 1);
                cTable.setValueAt(newA, row, 2);
                DS.msg(side, "Customer updated.");
            }
        });

        payBtn.addActionListener(e -> {
            int row = cTable.getSelectedRow();
            if (row < 0) { DS.msg(side, "Select a customer first."); return; }
            String nm = (String) cTable.getValueAt(row, 0);
            String amtStr = JOptionPane.showInputDialog("Payment amount:");
            String method  = JOptionPane.showInputDialog("Payment method:");
            if (amtStr == null || method == null) return;
            try {
                customerDatabase.addPayment(nm, Double.parseDouble(amtStr), method);
                DS.msg(side, "Payment recorded.");
            } catch (NumberFormatException ex) { DS.msg(side, "Invalid amount."); }
        });

        orderBtn.addActionListener(e -> {
            int row = cTable.getSelectedRow();
            if (row < 0) { DS.msg(side, "Select a customer first."); return; }
            String nm = (String) cTable.getValueAt(row, 0);
            String item = JOptionPane.showInputDialog("Item:");
            String qStr  = JOptionPane.showInputDialog("Quantity:");
            if (item == null || qStr == null) return;
            try {
                customerDatabase.addOrderToCustomer(nm, item, Integer.parseInt(qStr));
                DS.msg(side, "Order added.");
            } catch (NumberFormatException ex) { DS.msg(side, "Invalid quantity."); }
        });

        returnBtn.addActionListener(e -> frame.dispose());
        return side;
    }

    // ── Supplier Window ──────────────────────────────────
    private DefaultTableModel model;
    private JTable supplierTable;

    private void showSupplierDatabaseWindow() {
        JFrame frame = DS.frame("RESU IMS — Supplier Database", 1000, 640);
        frame.setLayout(new BorderLayout());

        if (supplierTable == null) {
            model = new DefaultTableModel(
                new Object[]{"Name","Contact","Address","Supplied Item","Expenditure","Date"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            supplierTable = DS.styledTable(new Object[]{"Name","Contact","Address","Supplied Item","Expenditure","Date"});
            supplierTable.setModel(model);
        }

        JPanel sidebar = buildSupplierSidebar(supplierTable, frame);
        SupplierDatabase sd = new SupplierDatabase();
        sd.loadFromFile();
        for (Supplier s : sd.getSuppliers())
            model.addRow(new Object[]{s.getName(), s.getContactInfo(), s.getAddress(),
                s.getSuppliedItem(), s.getTotalExpenditures(), s.getTransactionDate()});

        frame.add(sidebar,                  BorderLayout.WEST);
        frame.add(DS.scroll(supplierTable), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel buildSupplierSidebar(JTable sTable, JFrame frame) {
        JPanel side = new JPanel();
        side.setBackground(DS.BG_PANEL);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,0,1,DS.BORDER),
            BorderFactory.createEmptyBorder(24,20,24,20)));
        side.setPreferredSize(new Dimension(240,0));

        JLabel title = new JLabel("Suppliers");
        title.setFont(DS.FONT_HEAD);
        title.setForeground(DS.TEXT_PRI);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(title);
        side.add(Box.createRigidArea(new Dimension(0,4)));
        side.add(new AccentSeparator());
        side.add(Box.createRigidArea(new Dimension(0,18)));

        JTextField nameF    = DS.field("Supplier name");
        JTextField contactF = DS.field("xxx-xxx-xxxx");
        JTextField addrF    = DS.field("Address");
        JTextField itemF    = DS.field("Supplied item");
        JTextField expendF  = DS.field("Expenditure amount");
        JTextField dateF    = DS.field("yyyy/mm/dd");

        for (JTextField f : new JTextField[]{nameF, contactF, addrF, itemF, expendF, dateF}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        String[] labels = {"NAME","CONTACT","ADDRESS","SUPPLIED ITEM","EXPENDITURE","DATE"};
        JTextField[] fields = {nameF, contactF, addrF, itemF, expendF, dateF};
        for (int i = 0; i < labels.length; i++) {
            side.add(DS.labelSec(labels[i]));
            side.add(Box.createRigidArea(new Dimension(0,4)));
            side.add(fields[i]);
            side.add(Box.createRigidArea(new Dimension(0,8)));
        }
        side.add(Box.createRigidArea(new Dimension(0,10)));

        JButton addBtn    = DS.btn("Add Supplier");
        JButton updateBtn = DS.btnGhost("Update Selected");
        JButton returnBtn = DS.btnGhost("← Return");

        for (JButton b : new JButton[]{addBtn, updateBtn, returnBtn}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        side.add(addBtn);     side.add(Box.createRigidArea(new Dimension(0,8)));
        side.add(updateBtn);  side.add(Box.createVerticalGlue());
        side.add(returnBtn);

        addBtn.addActionListener(e -> {
            String nm = nameF.getText(), ci = contactF.getText(), ad = addrF.getText(),
                   it = itemF.getText(), dt = dateF.getText();
            if (nm.isEmpty()||ci.isEmpty()||ad.isEmpty()||it.isEmpty()||dt.isEmpty()) {
                DS.msg(side, "All fields must be filled."); return;
            }
            if (!ci.matches("\\d{3}-\\d{3}-\\d{4}")) {
                DS.msg(side, "Contact must be xxx-xxx-xxxx."); return;
            }
            double expAmt;
            try {
                expAmt = Double.parseDouble(expendF.getText());
                if (expAmt <= 0) { DS.msg(side, "Expenditure must be positive."); return; }
            } catch (NumberFormatException ex) { DS.msg(side, "Invalid expenditure."); return; }
            if (!Supplier.isValidDate(dt)) { DS.msg(side, "Date must be yyyy/mm/dd."); return; }

            ArrayList<Supplier> existing = supplierDatabase.searchSupplier(nm);
            for (Supplier s : existing) if (s.getContactInfo().equalsIgnoreCase(ci)) {
                DS.msg(side, "Supplier already exists."); return;
            }

            supplierDatabase.addSupplier(nm, ci, ad, it, expAmt, dt);
            model.addRow(new Object[]{nm, ci, ad, it,
                NumberFormat.getCurrencyInstance().format(expAmt), dt});
            supplierDatabase.saveToFile();
            for (JTextField f : fields) f.setText("");
            DS.msg(side, "Supplier added successfully.");
        });

        updateBtn.addActionListener(e -> {
            int row = sTable.getSelectedRow();
            if (row < 0) { DS.msg(side, "Select a supplier to update."); return; }
            String nm  = (String) sTable.getValueAt(row, 0);
            String cur = (String) sTable.getValueAt(row, 1);
            String newC = JOptionPane.showInputDialog("New contact info:", cur);
            if (newC != null && !newC.matches("\\d{3}-\\d{3}-\\d{4}")) {
                DS.msg(side, "Contact must be xxx-xxx-xxxx."); return;
            }
            String newA = JOptionPane.showInputDialog("New address:", sTable.getValueAt(row,2));
            String newI = JOptionPane.showInputDialog("New supplied item:", sTable.getValueAt(row,3));
            String newD = JOptionPane.showInputDialog("New date (yyyy/mm/dd):", sTable.getValueAt(row,5));
            if (!Supplier.isValidDate(newD)) { DS.msg(side, "Invalid date format."); return; }
            String newE = JOptionPane.showInputDialog("New expenditure:", sTable.getValueAt(row,4));
            double exp;
            try { exp = Double.parseDouble(newE); }
            catch (NumberFormatException ex) { DS.msg(side, "Invalid expenditure."); return; }

            supplierDatabase.updateSupplier(nm, cur, newC, newA, newI, newD, exp);
            model.setValueAt(newC, row, 1);
            model.setValueAt(newA, row, 2);
            model.setValueAt(newI, row, 3);
            model.setValueAt(NumberFormat.getCurrencyInstance().format(exp), row, 4);
            model.setValueAt(newD, row, 5);
            DS.msg(side, "Supplier updated.");
        });

        returnBtn.addActionListener(e -> frame.dispose());
        return side;
    }

    // ── Analytics Window ─────────────────────────────────
    public void showAnalyticsWindow() {
        InventoryUsageAnalytics analytics = new InventoryUsageAnalytics(stockInventory, customerDatabase);

        JFrame frame = DS.frame("RESU IMS — Analytics", 860, 560);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DS.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,DS.BORDER),
            BorderFactory.createEmptyBorder(16,24,16,24)));
        JLabel hTitle = new JLabel("Inventory Analytics");
        hTitle.setFont(DS.FONT_TITLE);
        hTitle.setForeground(DS.TEXT_PRI);
        header.add(hTitle, BorderLayout.WEST);

        // Report area
        JTextArea report = new JTextArea();
        report.setEditable(false);
        report.setBackground(DS.BG_CARD);
        report.setForeground(DS.TEXT_PRI);
        report.setFont(new Font("Monospaced", Font.PLAIN, 13));
        report.setCaretColor(DS.ACCENT);
        report.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        report.setText("Select an action below to generate or export a report.");

        // Button bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        btnBar.setBackground(DS.BG_PANEL);
        btnBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,DS.BORDER));

        JButton genBtn  = DS.btn("Generate Report");
        JButton expBtn  = DS.btn("Export Report");
        JButton dashBtn = DS.btnGhost("Show Dashboard");

        btnBar.add(genBtn);
        btnBar.add(expBtn);
        btnBar.add(dashBtn);

        genBtn.addActionListener(e  -> report.setText(analytics.generateReport("weekly")));
        expBtn.addActionListener(e  -> {
            String content = analytics.generateReport("monthly");
            report.setText(analytics.exportReport(content, "monthly")
                ? "Report exported successfully." : "Failed to export report.");
        });
        dashBtn.addActionListener(e -> {
            String content = analytics.generateReport("yearly");
            Map<String, Integer> data = new HashMap<>();
            customer.getPurchaseHistory();
            data.put("Item A", 10);
            data.put("Item B", 20);
            analytics.showReportInDashboard(content, data);
        });

        frame.add(header,              BorderLayout.NORTH);
        frame.add(DS.scroll(report),   BorderLayout.CENTER);
        frame.add(btnBar,              BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementSystemGUI());
    }
}
