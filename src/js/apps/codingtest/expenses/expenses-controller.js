"use strict";

/******************************************************************************************

Expenses controller

******************************************************************************************/

var app = angular.module("expenses.controller", []);

app.controller("ctrlExpenses", ["$rootScope", "$scope", "config", "restalchemy", "notifications", function ExpensesCtrl($rootScope, $scope, $config, $restalchemy, $notifications) {
	// Update the headings
	$rootScope.mainTitle = "Expenses";
	$rootScope.mainHeading = "Expenses";

	// Update the tab sections
	$rootScope.selectTabSection("expenses", 0);

	var restExpenses = $restalchemy.init({ root: $config.apiroot }).at("expenses");

	$scope.dateOptions = {
		changeMonth: true,
		changeYear: true,
		dateFormat: "dd/mm/yy"
	};

	var checkDate = function(str) {
		if (str && str.length === 10) {
            var m = str.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
            if (m) {
                var year = parseInt(m[3]);
                var month = parseInt(m[2]);
                var day = parseInt(m[1]);
                if (
                    year > 2000 && year < 2100 &&
                    month > 0 && month < 13 &&
                    day > 0 && day < 32
				)
                $scope.clearValidationMessage();
                return true;
            }
        }
		return false;
	};

	var loadExpenses = function() {
		// Retrieve a list of expenses via REST
		restExpenses.get().then(function(expenses) {
			$scope.expenses = expenses;
            $scope.loggedIn = true;
		});
	};

	var loadVatAndCurrencyData = function(vatCalculation) {
        $scope.newExpense.vatCalculation = vatCalculation;
        $scope.newExpense.vatCalculation.amountWithCurrency =
			vatCalculation.amount.toFixed(2) + ' ' + vatCalculation.currency.shortName;
        $scope.newExpense.vatCalculation.vatWithCurrency =
            vatCalculation.vatAmount.toFixed(2) + ' ' + vatCalculation.currency.shortName;
        $scope.clearValidationMessage();
    };

	var getVatAndCurrencyCalculation = function() {
        // Get calculation data via REST
		var params = {
			date: $scope.newExpense.date,
			amount: $scope.newExpense.amount
		};

		restExpenses.at('expenses/calculations').get(params)
			.then(function (vatCalculation) {
				loadVatAndCurrencyData(vatCalculation);
			})
			.error(function () {
				$scope.newExpense.vatCalculation = {};
			});
    };

    var logoutSuccess = function() {
        $scope.loggedIn = false;
        $scope.expenses = {};
        $scope.clearExpense();
        document.cookie = "JSESSIONID=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        $notifications.show("Logout", "You just logout", "info", 5000);
        loadExpenses();
    };

	$scope.saveExpense = function() {
		var isDateValid = checkDate($scope.newExpense.date);
		if ($scope.expensesform.$valid && isDateValid) {
			// Post the expense via REST
			restExpenses.post($scope.newExpense).then(function() {
				// Reload new expenses list
				loadExpenses();
			})
			.error(function (err) {
                $notifications.error("Error in form", err.message, {});
			});
		}
		if (!isDateValid) {
            $notifications.error("Error in form", "Date format is invalid", {});
		}
	};

    $scope.clearValidationMessage = function() {
        $notifications.clear();
    };

	$scope.clearExpense = function() {
		$scope.newExpense = {};
	};

	$scope.amountChange = function() {
		// get calculation only if there is amount or one of following:
		// - date is empty
		// - valid date format
        if ($scope.newExpense.amount &&
			(!$scope.newExpense.date || checkDate($scope.newExpense.date))) {
        	getVatAndCurrencyCalculation();
        } else {
            $scope.newExpense.vatCalculation = {};
        }
	};

    $scope.login = function() {
        loadExpenses();
    };

    $scope.logout = function() {
        $restalchemy.init({ root: $config.apiroot }).at("logout").get().then(logoutSuccess)
		.error(function (err, code) {
			if (code == 401) {
                logoutSuccess();
			} else {
                $notifications.error("Logout", "Error during logout: " + err, {});
			}
		});
    };

	// Initialise scope variables
	loadExpenses();
	$scope.clearExpense();
}]);
