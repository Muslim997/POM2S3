'use client';

import { useState, Suspense } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import Input from '@/components/Input';
import Button from '@/components/Button';
import { Textarea } from '@/components/Input';

function NewCoursePageContent() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    code: '',
    description: '',
    credits: 3,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/courses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        router.push('/courses');
      } else {
        alert('Erreur lors de la création du cours');
      }
    } catch (error) {
      console.error('Error creating course:', error);
      alert('Erreur lors de la création du cours');
    } finally {
      setLoading(false);
    }
  };

  if (!user || (user.role !== 'teacher' && user.role !== 'admin')) {
    router.push('/dashboard');
    return null;
  }

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Créer un nouveau cours</h1>
          <p className="text-gray-600">Ajoutez un nouveau cours à votre catalogue pédagogique</p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Informations du cours</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <Input
                label="Titre du cours"
                placeholder="Ex: Algorithmes et Structures de Données"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                required
              />

              <Input
                label="Code du cours"
                placeholder="Ex: INFO301"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                required
              />

              <Textarea
                label="Description"
                placeholder="Décrivez le contenu et les objectifs du cours..."
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={4}
                required
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Crédits ECTS
                </label>
                <select
                  value={formData.credits}
                  onChange={(e) => setFormData({ ...formData, credits: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value={1}>1 crédit</option>
                  <option value={2}>2 crédits</option>
                  <option value={3}>3 crédits</option>
                  <option value={4}>4 crédits</option>
                  <option value={5}>5 crédits</option>
                  <option value={6}>6 crédits</option>
                </select>
              </div>

              <div className="flex space-x-3">
                <Button type="submit" disabled={loading}>
                  {loading ? 'Création...' : 'Créer le cours'}
                </Button>
                <Button type="button" variant="secondary" onClick={() => router.back()}>
                  Annuler
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
}

export default function NewCoursePage() {
  return (
    <Suspense fallback={
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </Layout>
    }>
      <NewCoursePageContent />
    </Suspense>
  );
}