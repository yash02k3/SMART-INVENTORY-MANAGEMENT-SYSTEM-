# 🚀 Project Run Karne Ka Simple Guide (For Windows Users)

Yeh guide un doston ya teachers ke liye hai jinke paas **Windows laptop** hai aur unke PC me pehle se kuch bhi install nahi hai.

---

## 💻 Case 1: Windows Laptop me kuch bhi nahi hai toh kya karein?

Agar aapke dost ke Windows PC me **Java bhi nahi hai**, toh Terminal (Command Prompt ya PowerShell) open karke sirf yeh 1 command chalayein:

### Step 1: Java (JDK 17) Install karein (Sirf 1 minute)
Windows PowerShell ya CMD ko "Run as administrator" karke yeh type karein:
```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```
*(Yeh command Windows me officially bina kisi browser ke directly Java download & install kar degi!)*

---

### Step 2: Project Download karein (GitHub se)

CMD ya PowerShell me type karein:
```cmd
git clone https://github.com/yash02k3/SMART-INVENTORY-MANAGEMENT-SYSTEM-.git
cd SMART-INVENTORY-MANAGEMENT-SYSTEM-
```

> 💡 **Bina Git ke download karna ho toh?**
> Seedha GitHub link par jaakar **Code -> Download ZIP** par click karein aur folder ko Extract kar lein.

---

### Step 3: Run karein! (1-Click Run)

Project folder me jaakar:
- **`run.bat` file par DOUBLE CLICK karein!**

Bass! `run.bat` apne aap:
1. Java compile karega.
2. In-Memory backend server start karega (Zero database setup!).
3. Chrome / Edge browser me web dashboard automatically khol dega:
   👉 **`http://localhost:8080`**

---

## 🌐 Case 2: Agar Java bhi install nahi karna chahte?

Agar koi dost bolta hai ki "Mujhe Java install nahi karna", toh bhi tension nahi!
- Project folder ke andar **`web`** folder me jayein.
- **`index.html`** file par double-click karke Chrome ya Edge me open kar lein!
- Saara frontend dashboard, presentation wale saare products, search, category filter, low-stock alerts, aur multithreading simulator **bina kisi server ke browser me chalega**!

---

## 🗄️ Database ka kya scene hai?
Aapko kisi bhi MySQL ya database installation ki zaroorat **nahi** hai!
Humne poora project **In-Memory Collections Engine** ke sath build kiya hai jisme report ke saare 8 sample products pehle se bhare huye hain:
- USB Cable Type-C (Qty: 4) - Critical Alert
- Wireless Mouse (Qty: 2) - Critical Alert
- HDMI Adapter (Qty: 6) - Low Stock
- A4 Paper Ream (Qty: 265) - In Stock
- Gel Pen (Qty: 412) - In Stock
- Packing Box (Qty: 11) - Low Stock
- Bubble Wrap Roll (Qty: 180) - In Stock
- Office Chair Mesh (Qty: 95) - In Stock

---

## 🎯 Viva / Presentation me kya dikhana hai?
1. **Dashboard Tab**: KPI summary bar (1,248 Units, 4 Low stock alerts), Category distribution chart, aur Recent transactions.
2. **Item Inventory Tab**: Real-time search, Category dropdown, and Stock In (+In) / Stock Out (-Out) transactions.
3. **Multithreading Simulator Tab**: Ek sath 4 counters se concurrent checkout chala kar dikhana ki `synchronized` blocks kaise **overselling** ko 100% rok dete hain!
