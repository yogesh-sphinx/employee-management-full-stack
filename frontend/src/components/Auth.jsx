import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_URL } from '../config';

const API_BASE_URL = `${API_URL}/auth`;

export default function Auth() {
  const [isSignup, setIsSignup] = useState(true);
  const [formData, setFormData] = useState({ email: "", password: "", fullName: "" });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const endpoint = isSignup ? "/signup" : "/login";
    const payload = isSignup
      ? formData
      : { email: formData.email, password: formData.password };
  
    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
  
      if (!response.ok) {
        const errorData = await response.json(); // Read response once
        throw new Error(errorData.message || "Authentication failed");
      }
  
      if (isSignup) {
        // Signup success (200 OK)
        alert("Account Created Successfully! Please login.");
        setIsSignup(false); // Switch to login mode
        navigate("/login");
      } else {
        // Login success
        const data = await response.json(); // Read response once
        console.log("API Response:", data);
        localStorage.setItem("token", `Bearer ${data.token}`);
        navigate("/dashboard");
      }
    } catch (error) {
      alert(error.message || "Invalid credentials. Please try again.");
    }
  };
  
  return (
    <div>
      <h2>{isSignup ? "Signup" : "Login"}</h2>
      <form onSubmit={handleSubmit}>
        {isSignup && (
          <input type="text" name="fullName" placeholder="Full Name" value={formData.fullName} onChange={handleChange} required />
        )}
        <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} required />
        <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required />
        <button type="submit">{isSignup ? "Signup" : "Login"}</button>
      </form>
      <button onClick={() => setIsSignup(!isSignup)}>
        {isSignup ? "Already have an account? Login" : "Don't have an account? Signup"}
      </button>
    </div>
  );
}
