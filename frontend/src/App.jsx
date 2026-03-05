import React, { useState, useEffect } from "react";
import { ChevronDown } from "lucide-react";

export default function LandingPage() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username] = useState("Arunavo");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [bannerIndex, setBannerIndex] = useState(0);

  const banners = [
    "Increase resume ATS score",
    "Tailor resume to each job post",
    "Bulk apply",
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setBannerIndex((prev) => (prev + 1) % banners.length);
    }, 2500);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-gray-200 flex flex-col">
      {/* Navbar */}
      <nav className="flex justify-between items-center px-8 py-4 bg-lavender-500 bg-[#C8A2C8] shadow-md">
        <h1 className="text-xl font-semibold text-gray-800">Job AI Assistant</h1>

        {!isLoggedIn ? (
          <button
            onClick={() => setIsLoggedIn(true)}
            className="bg-gray-700 text-white px-4 py-2 rounded-xl hover:bg-gray-800 transition"
          >
            Login
          </button>
        ) : (
          <div className="relative">
            <button
              onClick={() => setDropdownOpen(!dropdownOpen)}
              className="flex items-center gap-2 bg-gray-700 text-white px-4 py-2 rounded-xl"
            >
              {username}
              <ChevronDown size={16} />
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-40 bg-white rounded-xl shadow-lg">
                <button className="block w-full text-left px-4 py-2 hover:bg-gray-100">
                  Profile
                </button>
                <button className="block w-full text-left px-4 py-2 hover:bg-gray-100">
                  Settings
                </button>
                <button
                  onClick={() => {
                    setIsLoggedIn(false);
                    setDropdownOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        )}
      </nav>

      {/* Hero Section */}
      <div className="flex flex-1 flex-col items-center justify-center text-center px-4">
        <h2 className="text-4xl md:text-5xl font-bold text-gray-800 mb-6">
          Power up your application process with AI
        </h2>

        <div className="bg-[#C8A2C8] text-gray-900 px-8 py-4 rounded-2xl shadow-xl text-xl font-medium transition-all duration-500">
          {banners[bannerIndex]}
        </div>

        {/* Orchestrator Test Button */}
        <div className="mt-10">
          <button
            onClick={async () => {
              try {
                const response = await fetch("http://localhost:8080/api/orchestrator/health");
                const data = await response.json();
                alert(`Status: ${data.status}\nTime: ${data.timestamp}`);
              } catch (error) {
                alert("Failed to connect to Orchestrator Service");
              }
            }}
            className="bg-gray-800 text-white px-6 py-3 rounded-2xl hover:bg-gray-900 transition shadow-lg"
          >
            Test Orchestrator API
          </button>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-[#C8A2C8] text-center py-4 text-gray-800">
        © {new Date().getFullYear()} Job AI Assistant
      </footer>
    </div>
  );
}
