# 🏪 IMS - Inventory Management System

A comprehensive **Java-based Inventory Management System** with multi-level user access (CEO → Manager → Cashier), built with Swing GUI and SQLite database.

## 📥 Quick Installation

### Download & Install

**[📦 Download IMS-Installer.zip](https://github.com/abdulrafay1402/IMS--Inventory-Management-System-/releases/latest/download/IMS-Installer.zip)**

1. **Extract** the downloaded ZIP file
2. **Run** `install.bat` as Administrator
   - Right-click → "Run as administrator"
3. Follow the **6-step installation wizard**
4. Launch from **Desktop shortcut** or **Start Menu**

### Default Login Credentials

```
Username: ceo
Password: ceo123
```

> 💡 **Important:** Change the default password after first login from the profile settings!

---

## 🎯 Features

### ✨ Core Functionality
- **📊 Master Inventory Management** - CEO controls all inventory
- **👥 Multi-Level User System** - CEO → Manager → Cashier hierarchy
- **🧾 Bill Creation & Tracking** - Complete billing system with history
- **💰 Expense Management** - Track and categorize business expenses
- **📈 Financial Reports** - Profit analysis, sales reports, performance metrics
- **🔄 Stock Transfer** - Transfer inventory between managers
- **🗄️ Embedded Database** - No external database server needed (SQLite)

### 🔐 User Roles

#### CEO (Chief Executive Officer)
- Full system access
- Create manager accounts
- Manage master inventory
- View all financial reports
- System-wide oversight

#### Manager
- Manage assigned inventory
- Request and approve cashiers
- Transfer stock between managers
- View manager-level reports
- Add expenses

#### Cashier
- Create bills and process sales
- View assigned inventory
- View past bills
- Basic operations only

---

## 💻 System Requirements

| Requirement | Specification |
|------------|---------------|
| **Operating System** | Windows 7 or later |
| **Java** | JRE/JDK 17 or higher |
| **Disk Space** | 50 MB minimum |
| **RAM** | 512 MB minimum |
| **Privileges** | Administrator (for installation only) |

---

## 🚀 Installation Guide

### Step 1: Download
Download the latest release from the [Releases page](https://github.com/abdulrafay1402/IMS--Inventory-Management-System-/releases)

### Step 2: Extract
Extract `IMS-Installer.zip` to any location on your computer

### Step 3: Run Installer
```
Right-click install.bat → Run as administrator
```

### Step 4: Installation Wizard
The installer will guide you through:
1. ✅ System compatibility check
2. ☕ Java version verification
3. 🗄️ Database initialization
4. 🖥️ Desktop shortcut creation
5. 📋 Installation summary
6. ✅ Completion

### Step 5: Launch Application
- Use the **Desktop shortcut**, or
- Launch from **Start Menu**, or
- Run `IMS.jar` directly

---

## 📖 User Guide

### First Time Setup

1. **Login with CEO credentials**
   ```
   Username: ceo
   Password: ceo123
   ```

2. **Change Default Password**
   - Click profile icon → Change password
   - Use strong password

3. **Add Inventory Items**
   - Go to Master Inventory
   - Add products with prices and quantities

4. **Create Manager Accounts**
   - Go to "Add Manager" panel
   - Provide manager details
   - Manager can now login

5. **Managers Create Cashiers**
   - Manager logs in
   - Goes to "Request Cashier" panel
   - Creates cashier account
   - Cashier can now login

### Daily Operations

#### For CEO
- Monitor master inventory
- Review financial reports
- Manage managers
- Oversee all operations

#### For Manager
- Manage assigned inventory
- Approve cashier requests
- Add business expenses
- Transfer stock between locations
- View manager performance

#### For Cashier
- Create customer bills
- Process sales
- View inventory
- Check past transactions

---

## 🛠️ For Developers

### Project Structure
```
IMS/
├── src/                    # Java source code
│   ├── database/          # DAO classes
│   ├── models/            # Data models
│   ├── ui/                # Swing GUI components
│   └── utils/             # Utility classes
├── data/                  # Database initialization scripts
├── image/                 # Logo and icons
├── lib/                   # SQLite JDBC driver
├── dev-tools/             # Build scripts
│   └── build.bat         # Compile and create JAR
└── install.bat           # Windows installer

```

### Building from Source

1. **Clone Repository**
   ```bash
   git clone https://github.com/abdulrafay1402/IMS--Inventory-Management-System-.git
   cd IMS--Inventory-Management-System-/IMS
   ```

2. **Build JAR**
   ```bash
   cd dev-tools
   build.bat
   ```
   This will:
   - Compile all Java files
   - Package into executable JAR
   - Include all resources (images, database scripts)

3. **Run Application**
   ```bash
   cd ..
   java -jar IMS.jar
   ```

### Database Schema

The system uses **SQLite** with the following main tables:
- `users` - User accounts (CEO, Manager, Cashier)
- `ceo_inventory` - Master inventory
- `manager_inventory` - Manager-assigned inventory
- `bills` - Sales transactions
- `bill_items` - Individual bill line items
- `expenses` - Business expenses
- `stock_transfer` - Inventory transfers

Schema files located in: `IMS/data/schema.sql`

---

## 🔧 Troubleshooting

### Common Issues

**Q: "Java not found" error during installation**
- Install Java 17 or higher from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)

**Q: Login fails with correct credentials**
- Ensure database is initialized
- Check `app/data/inventory_db.sqlite` exists
- Re-run installer to reinitialize database

**Q: Images not showing in application**
- This is fixed in the latest version
- Redownload `IMS-Installer.zip`

**Q: Permission denied errors**
- Run installer as Administrator
- Check antivirus isn't blocking the application

---

## 📦 Creating Distribution Package

To create the installer ZIP for distribution:

```powershell
cd IMS
Compress-Archive -Path "IMS.jar","install.bat","image","data","README.txt" -DestinationPath "IMS-Installer.zip" -CompressionLevel Optimal
```

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

## 📄 License

This project is available for educational and commercial use.

---

## 👨‍💻 Developer

**Abdul Rafay**
- GitHub: [@abdulrafay1402](https://github.com/abdulrafay1402)

---

## 🌟 Support

If you find this project helpful, please give it a ⭐ on GitHub!

For issues and feature requests, please use the [Issues](https://github.com/abdulrafay1402/IMS--Inventory-Management-System-/issues) page.

---

## 📸 Screenshots

*Coming soon - Application interface screenshots*

---

**Made with ❤️ using Java Swing**
