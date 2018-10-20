"use strict";

/******************************************************************************************

Expenses controller

******************************************************************************************/

var app = angular.module("expenses.controller", []);

app.controller("ctrlExpenses", ["$rootScope", "$scope", "config", "restalchemy", function ExpensesCtrl($rootScope, $scope, $config, $restalchemy) {
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

	var loadExpenses = function() {
		// Retrieve a list of expenses via REST
		restExpenses.get().then(function(expenses) {
			$scope.expenses = expenses;
		});
	};

	var loadVatAndCurrencyData = function(vatCalculation) {
        $scope.newExpense.vatCalculation = vatCalculation;
        $scope.newExpense.vatCalculation.amountWithCurrency =
			vatCalculation.amount.toFixed(2) + ' ' + vatCalculation.currency.shortName;
        $scope.newExpense.vatCalculation.vatWithCurrency =
            vatCalculation.vatAmount.toFixed(2) + ' ' + vatCalculation.currency.shortName;
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

	$scope.saveExpense = function() {
		if ($scope.expensesform.$valid) {
			// Post the expense via REST
			restExpenses.post($scope.newExpense).then(function() {
				// Reload new expenses list
				loadExpenses();
			});
		}
	};

	$scope.clearExpense = function() {
		$scope.newExpense = {};
	};

	$scope.amountChange = function() {
        if ($scope.newExpense.amount) {
        	getVatAndCurrencyCalculation();
        } else {
            $scope.newExpense.vatCalculation = {};
        }
	};

	// Initialise scope variables
	loadExpenses();
	$scope.clearExpense();
}]);
