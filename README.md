### **e-commerce-fullstack_springboot_angular_aws**  
🚀 Full-stack e-commerce application built with Spring Boot, Angular, Spring Security 6, JWT, and AWS deployment.  

---

## 📌 **Project Overview**  
This is a full-stack e-commerce application with the following features:  
- 🛂 **Backend:** Spring Boot, Spring Data JPA, Spring Security 6, JWT authentication, REST APIs  
- 🎨 **Frontend:** Angular, Bootstrap 5  
- ☁ **Deployment:** AWS (S3, EC2, RDS, CloudFront)  
- 🟢 **Database:** PostgreSQL / MySQL  
- 🔒 **Security:** JWT authentication, role-based access control  
- 🏢 **CI/CD:** GitHub Actions, Docker, Kubernetes  

---

## 🏠 **Project Structure**  
```
e-commerce-fullstack_springboot_angular_aws/
│── backend/                  # Spring Boot application (REST APIs, Security, DB)
│   ├── src/main/java/...      # Java source code
│   ├── src/main/resources/    # Configuration files
│   ├── pom.xml                # Maven dependencies
│── frontend/                  # React app (UI, state management, API calls)
│   ├── src/                   # React source code
│   ├── public/                # Static assets
│   ├── package.json           # Node.js dependencies
│── README.md                  # Project documentation
│── .gitignore                 # Ignore unnecessary files
│── docker-compose.yml         # Docker setup (if needed)
│── deploy/                    # Deployment scripts
```

---

## 🚀 **Installation & Setup**  

### **1️⃣ Clone the Repository**  
```sh
git clone https://github.com/dinesh-more/e-commerce-fullstack_springboot_angular_aws.git
cd e-commerce-fullstack_springboot_angular_aws
```

### **2️⃣ Backend Setup**  
- **Navigate to Backend Folder:**  
  ```sh
  cd backend
  ```
- **Configure Database:** Update `application.properties` or `application.yml`  
- **Run the Application:**  
  ```sh
  mvn spring-boot:run
  ```

### **3️⃣ Frontend Setup**  
- **Navigate to Frontend Folder:**  
  ```sh
  cd frontend
  ```
- **Install Dependencies:**  
  ```sh
  npm install
  ```
- **Start Development Server:**  
  ```sh
  ng serve
  ```

---

## 💽 **API Endpoints**  
| Method | Endpoint               | Description                 |  
|--------|------------------------|-----------------------------|  
| POST   | `/api/auth/signup`     | User registration           |  
| POST   | `/api/auth/login`      | User login & JWT token      |  
| GET    | `/api/products`        | Fetch all products         |  
| GET    | `/api/products/{id}`   | Fetch product by ID        |  
| POST   | `/api/orders`          | Place an order             |  

---

## 🌍 **Deployment on AWS**  
- **Frontend:** Deployed on S3 & CloudFront  
- **Backend:** Running on EC2 with RDS database  
- **Docker Support:** Containerized app using Docker & Kubernetes  

---

## 🛠 **Tech Stack**  
### **Backend:**  
- Spring Boot  
- Spring Data JPA  
- Spring Security 6 & JWT  
- PostgreSQL / MySQL  
- Hibernate  
- Maven  

### **Frontend:**  
- Angular    
- Bootstrap 5

### **Cloud & DevOps:**  
- AWS S3, EC2, RDS, CloudFront  
- Docker & Kubernetes  
- CI/CD with GitHub Actions  

---

## 📌 **Future Enhancements**  
- ✅ Payment Gateway Integration (Razorpay/Stripe)  
- ✅ Admin Dashboard  
- ✅ Product Recommendations with AI  

---

## 🤝 **Contributing**  
Feel free to fork this repo, raise issues, and submit pull requests! 🎉  

---

## 📝 **License**  
This project is licensed under the MIT License.  
