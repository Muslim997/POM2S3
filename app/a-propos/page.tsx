'use client';

import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { GraduationCap, Users, BookOpen, Award, MapPin, Calendar } from 'lucide-react';

export default function AboutPage() {
  return (
    <Layout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            À propos de CampusMaster
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            La plateforme pédagogique digitale de référence pour les étudiants de Master 2 à l'Université Cheikh Anta Diop de Dakar
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-16">
          <div>
            <h2 className="text-3xl font-bold text-gray-900 mb-6">Notre Mission</h2>
            <p className="text-gray-600 mb-6">
              CampusMaster révolutionne l'enseignement supérieur au Sénégal en offrant une plateforme
              digitale moderne qui facilite l'apprentissage, la communication et la gestion académique.
              Notre objectif est de créer un environnement d'apprentissage inclusif et accessible à tous
              les étudiants de l'UCAD.
            </p>
            <p className="text-gray-600">
              Développée spécifiquement pour les programmes de Master 2 en Informatique, Mathématiques
              et Physique, notre plateforme intègre les dernières technologies pour offrir une expérience
              utilisateur exceptionnelle.
            </p>
          </div>

          <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-8">
            <div className="flex items-center mb-6">
              <GraduationCap className="text-blue-600 mr-3" size={32} />
              <h3 className="text-2xl font-bold text-gray-900">UCAD Dakar</h3>
            </div>
            <p className="text-gray-700 mb-4">
              L'Université Cheikh Anta Diop de Dakar est la plus ancienne et prestigieuse université
              d'Afrique de l'Ouest, fondée en 1918.
            </p>
            <div className="flex items-center text-gray-600">
              <MapPin size={16} className="mr-2" />
              Dakar, Sénégal
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-16">
          <Card>
            <CardHeader className="text-center">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Users className="text-blue-600" size={24} />
              </div>
              <CardTitle>500+ Étudiants</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-center text-gray-600">
                Étudiants actifs sur la plateforme, répartis dans nos trois départements
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="text-center">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <BookOpen className="text-green-600" size={24} />
              </div>
              <CardTitle>12 Cours</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-center text-gray-600">
                Cours de Master 2 dispensés cette année académique
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="text-center">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Award className="text-purple-600" size={24} />
              </div>
              <CardTitle>15 Enseignants</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-center text-gray-600">
                Professeurs et docteurs impliqués dans l'enseignement
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="text-center">
              <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Calendar className="text-orange-600" size={24} />
              </div>
              <CardTitle>2 Semestres</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-center text-gray-600">
                Semestres académiques par année universitaire
              </p>
            </CardContent>
          </Card>
        </div>

        <div className="bg-gray-50 rounded-lg p-8 mb-16">
          <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">Nos Départements</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <BookOpen className="text-blue-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Informatique</h3>
              <p className="text-gray-600">
                Intelligence Artificielle, Développement Web, Big Data et Cybersécurité
              </p>
            </div>

            <div className="text-center">
              <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Award className="text-green-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Mathématiques</h3>
              <p className="text-gray-600">
                Statistiques avancées, Recherche opérationnelle et Analyse numérique
              </p>
            </div>

            <div className="text-center">
              <div className="w-20 h-20 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Users className="text-purple-600" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Physique</h3>
              <p className="text-gray-600">
                Physique quantique, Nanotechnologies et Physique des matériaux
              </p>
            </div>
          </div>
        </div>

        <div className="text-center">
          <h2 className="text-3xl font-bold text-gray-900 mb-6">Rejoignez-nous</h2>
          <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            CampusMaster est plus qu'une plateforme - c'est une communauté d'apprentis et d'enseignants
            engagés dans l'excellence académique au Sénégal.
          </p>
          <div className="flex justify-center space-x-4">
            <a
              href="/register"
              className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium transition-colors"
            >
              S'inscrire maintenant
            </a>
            <a
              href="/login"
              className="px-8 py-3 bg-white text-blue-600 border-2 border-blue-600 rounded-lg hover:bg-blue-50 font-medium transition-colors"
            >
              Se connecter
            </a>
          </div>
        </div>
      </div>
    </Layout>
  );
}