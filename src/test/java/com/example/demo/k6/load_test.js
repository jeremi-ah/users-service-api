import http from 'k6/http';
import { check, group, sleep } from 'k6';

// JSON configuration directly embedded
const config = {
  "options": {
    "stages": [
      { "duration": "30s", "target": 10 },
      { "duration": "1m", "target": 10 },
      { "duration": "30s", "target": 0 }
    ]
  },
  "scenarios": {
    "user_management": {
      "executor": "constant-vus",
      "vus": 10,
      "duration": "1m",
      "create_user": {
        "method": "POST",
        "url": "http://localhost:8080/api/users",
        "body": {
          "name": "John Doe",
          "email": "john.doe{{rand}}@example.com"
        },
        "headers": {
          "Content-Type": "application/json"
        }
      },
      "get_users": {
        "method": "GET",
        "url": "http://localhost:8080/api/users",
        "headers": {
          "Content-Type": "application/json"
        }
      },
      "get_user_by_id": {
        "method": "GET",
        "url": "http://localhost:8080/api/users/1",
        "headers": {
          "Content-Type": "application/json"
        }
      },
      "update_user": {
        "method": "PUT",
        "url": "http://localhost:8080/api/users/1",
        "body": {
          "name": "John Updated",
          "email": "john.updated@example.com"
        },
        "headers": {
          "Content-Type": "application/json"
        }
      },
      "delete_user": {
        "method": "DELETE",
        "url": "http://localhost:8080/api/users/1",
        "headers": {
          "Content-Type": "application/json"
        }
      }
    }
  }
};

export let options = config.options;

export default function () {
  group('User Management API', function () {
    // Create a new user with a random email
    let createResponse = http.post(config.scenarios.user_management.create_user.url, 
      JSON.stringify({
        name: config.scenarios.user_management.create_user.body.name,
        email: `john.doe${Math.random().toString(36).substring(7)}@example.com`  // Correct random email generation
      }), {
        headers: { 'Content-Type': 'application/json' },
      });

    check(createResponse, {
      'Create user status is 200': (r) => r.status === 200,
    });
    sleep(1);

    // Get all users
    let allUsersResponse = http.get(config.scenarios.user_management.get_users.url);
    check(allUsersResponse, {
      'Get all users status is 200': (r) => r.status === 200,
    });
    sleep(1);

    // Get user by ID (assuming user created above has ID 1)
    let getUserResponse = http.get(config.scenarios.user_management.get_user_by_id.url);
    check(getUserResponse, {
      'Get user by ID status is 200': (r) => r.status === 200,
    });
    sleep(1);

    // Update user
    let updateResponse = http.put(config.scenarios.user_management.update_user.url, 
      JSON.stringify({
        name: config.scenarios.user_management.update_user.body.name,
        email: config.scenarios.user_management.update_user.body.email
      }), {
        headers: { 'Content-Type': 'application/json' },
      });
    check(updateResponse, {
      'Update user status is 200': (r) => r.status === 200,
    });
    sleep(1);

    // Delete user
    let deleteResponse = http.del(config.scenarios.user_management.delete_user.url);
    check(deleteResponse, {
      'Delete user status is 200': (r) => r.status === 200,
    });
    sleep(1);
  });
}
