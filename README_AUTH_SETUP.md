# Authentication Setup Between Post-Service and User-Service

## Current Implementation

The post-service now has the following authentication endpoints:
- `/posts/login` - Redirects to user-service login page
- `/posts/auth-callback` - Handles authentication callback from user-service
- `/posts/logout` - Logs out the user

## Required Changes in User-Service

To complete the authentication flow, you need to modify your user-service login controller to:

1. **Accept a returnUrl parameter** in the login form
2. **Redirect back to the post-service** after successful login with the token and username

### Step 1: Modify User-Service Login Controller

In your user-service, update the login controller to handle the return URL:

```java
@PostMapping("/login")
public String login(@RequestParam String username, 
                   @RequestParam String password, 
                   @RequestParam(required = false) String returnUrl,
                   HttpSession session,
                   RedirectAttributes redirectAttributes) {
    
    // Your existing login logic here
    // After successful authentication:
    
    if (returnUrl != null && returnUrl.startsWith("http://localhost:8083")) {
        // Redirect back to post-service with token and username
        return "redirect:" + returnUrl + "?token=" + generatedToken + "&username=" + username;
    }
    
    // Default redirect for local login
    return "redirect:/success";
}
```

### Step 2: Update User-Service Login Template

In your user-service login.html, add a hidden field for the return URL:

```html
<form th:action="@{/login}" method="post">
    <input type="hidden" name="returnUrl" th:value="${param.returnUrl}"/>
    <!-- Your existing login form fields -->
</form>
```

### Step 3: Update User-Service Login Page Controller

```java
@GetMapping("/login-page")
public String loginPage(@RequestParam(required = false) String returnUrl, Model model) {
    model.addAttribute("returnUrl", returnUrl);
    return "login";
}
```

## How It Works

1. User clicks "Login" in post-service → redirects to `/posts/login`
2. Post-service redirects to user-service: `http://localhost:8082/login-page?returnUrl=http://localhost:8083/posts/auth-callback`
3. User logs in successfully in user-service
4. User-service redirects back to post-service: `http://localhost:8083/posts/auth-callback?token=xxx&username=xxx`
5. Post-service stores the token and username in session
6. User is redirected to `/posts/all` and sees their name at the top

## Testing

1. Start both services (user-service on 8082, post-service on 8083)
2. Go to `http://localhost:8083/posts/all`
3. Click "Login" → should redirect to user-service
4. Login with valid credentials → should redirect back to post-service
5. You should see "Welcome, [username]" at the top
6. The token is stored in session for API calls

## Security Notes

- The returnUrl is validated to only allow redirects to localhost:8083
- Tokens are stored in HTTP session (consider using secure cookies in production)
- This is a basic implementation - consider adding CSRF protection and other security measures 