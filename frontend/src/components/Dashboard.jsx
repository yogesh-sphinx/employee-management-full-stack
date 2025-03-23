import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080/api/employees";

const Dashboard = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    department: "",
    salary: "",
  });

  // Fetch employees on mount
  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(API_BASE_URL, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: token,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch employees");
        }

        const data = await response.json();
        setEmployees(data);
      } catch (error) {
        console.error("Error fetching employees:", error.message);
      }
    };

    fetchEmployees();
  }, []);

  // Handle form input changes
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Add or Update Employee
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("token");
      const method = editingEmployee ? "PUT" : "POST";
      const url = editingEmployee
        ? `${API_BASE_URL}/${editingEmployee.id}`
        : API_BASE_URL;

      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
        body: JSON.stringify(formData),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || "Failed to save employee");
      }

      if (editingEmployee) {
        // Update employee in state
        setEmployees(
          employees.map((emp) => (emp.id === editingEmployee.id ? result : emp))
        );
      } else {
        // Add new employee
        setEmployees([...employees, result]);
      }

      setShowForm(false);
      setEditingEmployee(null);
      setFormData({ name: "", email: "", department: "", salary: "" });
    } catch (error) {
      alert(error.message);
    }
  };

  // Handle Edit Click
  const handleEdit = (employee) => {
    setEditingEmployee(employee);
    setFormData({
      name: employee.name,
      email: employee.email, // Email is read-only
      department: employee.department,
      salary: employee.salary,
    });
    setShowForm(true);
  };

  // Handle Delete
  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: token,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to delete employee");
      }

      // Remove from state
      setEmployees(employees.filter((emp) => emp.id !== id));
    } catch (error) {
      alert(error.message);
    }
  };

  const handleLogout = () => {
    console.log("called")
    localStorage.clear(); // Clear all local storage
    navigate("/"); // Redirect to login page
    console.log("called2")
  };

  return (
    <div>
      <h2>Employee List</h2>
      {/* Logout Button */}
      <button onClick={handleLogout} style={{ float: "right", marginBottom: "10px" }}>
        Logout
      </button>
      <button
        onClick={() => {
          setShowForm(!showForm);
          setEditingEmployee(null);
        }}
      >
        Add Employee
      </button>

      {showForm && (
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            name="name"
            placeholder="Name"
            value={formData.name}
            onChange={handleChange}
            required
          />
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
            readOnly={editingEmployee !== null} // Read-only when editing
            style={editingEmployee ? { backgroundColor: "#f0f0f0" } : {}}
          />
          <input
            type="text"
            name="department"
            placeholder="Department"
            value={formData.department}
            onChange={handleChange}
            required
          />
          <input
            type="number"
            name="salary"
            placeholder="Salary"
            value={formData.salary}
            onChange={handleChange}
            required
          />
          <button type="submit">
            {editingEmployee ? "Update Employee" : "Add Employee"}
          </button>
        </form>
      )}

      <table border="1">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Department</th>
            <th>Salary</th>
            <th>Created At</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id}>
              <td>{emp.id}</td>
              <td>{emp.name}</td>
              <td>{emp.email}</td>
              <td>{emp.department}</td>
              <td>{emp.salary}</td>
              <td>{new Date(emp.createdAt).toLocaleString()}</td>
              <td>
                <button onClick={() => handleEdit(emp)}>Edit</button>
                <button onClick={() => handleDelete(emp.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Dashboard;
