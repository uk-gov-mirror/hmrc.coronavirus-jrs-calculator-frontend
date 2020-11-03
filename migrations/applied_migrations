#!/bin/bash

echo ""
echo "Applying migration resetCalculation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /resetCalculation                        controllers.resetCalculationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /resetCalculation                        controllers.resetCalculationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeresetCalculation                  controllers.resetCalculationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeresetCalculation                  controllers.resetCalculationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "resetCalculation.title = resetCalculation" >> ../conf/messages.en
echo "resetCalculation.heading = resetCalculation" >> ../conf/messages.en
echo "resetCalculation.checkYourAnswersLabel = resetCalculation" >> ../conf/messages.en
echo "resetCalculation.error.required = Select yes if resetCalculation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryresetCalculationUserAnswersEntry: Arbitrary[(resetCalculationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[resetCalculationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryresetCalculationPage: Arbitrary[resetCalculationPage.type] =";\
    print "    Arbitrary(resetCalculationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(resetCalculationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration resetCalculation completed"
