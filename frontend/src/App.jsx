import React, { useState, useEffect } from "react";
import { ChevronDown } from "lucide-react";

export default function LandingPage() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
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

  useEffect(() => {
    // Initialize Google Sign-In
    if (window.google) {
      window.google.accounts.id.initialize({
        client_id: 'YOUR_GOOGLE_CLIENT_ID', // Replace with your actual client ID
        callback: handleCredentialResponse
      });
      window.google.accounts.id.renderButton(
        document.getElementById('google-signin-button'),
        { theme: 'outline', size: 'large' }
      );
    }
  }, []);

  
    // Decode the JWT token to get user info
    /*const decoded = JSON.parse(atob(response.credential.split('.')[1]));
    setUser(decoded);
    setIsLoggedIn(true);*/
  const handleCredentialResponse = async (response) => {
      try {
        const res = await fetch("http://localhost:8080/api/orchestrator/Auth/google", {
          method: "POST",
          headers:{
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ 
            token: response.credential
          }),
          }
        );

        const data = await res.json();

        //Save jwt token recieved from backend to local storage
        localStorage.setItem("jwtToken", data.jwt);

        setUser(data.user);
        setIsLoggedIn(true);
      } catch (error) {
        console.error("Google Sign-In failed", error);
      }
  };

  return (
    <div className="min-h-screen bg-gray-200 flex flex-col">
      {/* Navbar */}
      <nav className="w-full max-w-screen-xl mx-auto flex justify-between items-center px-4 sm:px-6 md:px-8 py-4 bg-[#C8A2C8] shadow-md">
        <h1 className="text-lg sm:text-xl md:text-2xl font-semibold text-gray-800">Job AI Assistant</h1>

        {!isLoggedIn ? (
          <div id="google-signin-button"></div>
        ) : (
          <div className="relative">
            <button
              onClick={() => setDropdownOpen(!dropdownOpen)}
              className="flex items-center gap-2 bg-gray-700 text-white px-3 sm:px-4 py-2 rounded-xl text-sm sm:text-base"
            >
              {user?.name || 'User'}
              <ChevronDown size={16} />
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-32 sm:w-40 bg-white rounded-xl shadow-lg">
                <button className="block w-full text-left px-3 sm:px-4 py-2 hover:bg-gray-100 text-sm sm:text-base">
                  Profile
                </button>
                <button className="block w-full text-left px-3 sm:px-4 py-2 hover:bg-gray-100 text-sm sm:text-base">
                  Settings
                </button>
                <button
                  onClick={() => {
                    setIsLoggedIn(false);
                    setUser(null);
                    setDropdownOpen(false);
                  }}
                  className="block w-full text-left px-3 sm:px-4 py-2 hover:bg-gray-100 text-sm sm:text-base"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        )}
      </nav>

      {/* Hero Section */}
      <div className="flex flex-1 flex-col items-center justify-center text-center px-4 sm:px-6 md:px-8 w-full max-w-screen-xl mx-auto">
        <h2 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold text-gray-800 mb-6">
          Power up your application process with AI
        </h2>

        <div className="bg-[#C8A2C8] text-gray-900 px-6 sm:px-8 py-3 sm:py-4 rounded-2xl shadow-xl text-lg sm:text-xl font-medium transition-all duration-500 max-w-xs sm:max-w-md md:max-w-lg">
          {banners[bannerIndex]}
        </div>

        {/* Orchestrator Test Button */}
        <div className="mt-8 sm:mt-10">
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
            className="bg-gray-800 text-white px-4 sm:px-6 py-2 sm:py-3 rounded-2xl hover:bg-gray-900 transition shadow-lg text-sm sm:text-base"
          >
            Test Orchestrator API
          </button>
        </div>
      </div>

      {/* Footer */}
      <footer className="w-full max-w-screen-xl mx-auto bg-[#C8A2C8] text-center py-3 sm:py-4 text-gray-800 text-sm sm:text-base">
        © {new Date().getFullYear()} Job AI Assistant
      </footer>
    </div>
  );
}